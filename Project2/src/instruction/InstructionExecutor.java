package instruction;

import resource.Memory;
import resource.Register;
import resource.ResourceManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 명령어에 따라 수행할 동작을 정의한 모듈
 * ResourceManager의 상태를 적절히 변경함
 */
public class InstructionExecutor {
    public static class ProgramEndException extends Exception {
    }

    private final FileInputStream inputDevice;
    private final FileOutputStream outputDevice;

    public InstructionExecutor(String inputFileName, String outputFileName) throws FileNotFoundException {
        this.inputDevice = new FileInputStream(inputFileName);
        this.outputDevice = new FileOutputStream(outputFileName);
    }

    public void executeFormat2Inst(Instruction inst, String fullInst, ResourceManager resource) {
        int rest = Integer.parseInt(fullInst.substring(2, 4), 16);
        int n1 = (rest >> 4) & 0xF;
        int n2 = rest & 0xF;
        switch (inst.getName()) {
            case "ADDR" -> {
                // r2 <- (r2) + (r1)
                Register r1 = resource.getRegister(n1);
                Register r2 = resource.getRegister(n2);
                r2.setValue(r2.getValue() + r1.getValue());
            }
            case "SUBR" -> {
                // r2 <- (r2) - (r1)
                Register r1 = resource.getRegister(n1);
                Register r2 = resource.getRegister(n2);
                r2.setValue(r2.getValue() - r1.getValue());
            }
            case "MULR" -> {
                // r2 <- (r2) * (r1)
                Register r1 = resource.getRegister(n1);
                Register r2 = resource.getRegister(n2);
                r2.setValue(r2.getValue() * r1.getValue());
            }
            case "DIVR" -> {
                // r2 <- (r2) / (r1)
                Register r1 = resource.getRegister(n1);
                Register r2 = resource.getRegister(n2);
                r2.setValue(r2.getValue() / r1.getValue());
            }
            case "CLEAR" -> {
                // r1 <- 0
                Register r1 = resource.getRegister(n1);
                r1.setValue(0);
            }
            case "COMPR" -> {
                // (r1):(r2)
                Register r1 = resource.getRegister(n1);
                Register r2 = resource.getRegister(n2);
                Register sw = resource.getRegister(9);
                // > == < 순서
                if (r1.getValue() == r2.getValue())
                    sw.setValue(0x010);
                else if (r1.getValue() > r2.getValue())
                    sw.setValue(0x100);
                else
                    sw.setValue(0x001);
            }
            case "RMO" -> {
                // r2 <- (r1)
                Register r1 = resource.getRegister(n1);
                Register r2 = resource.getRegister(n2);
                r2.setValue(r1.getValue());
            }
            case "TIXR" -> {
                // X <- (X) + 1, (X):(r1)
                int xReg = resource.getRegister(1).getValue();
                resource.getRegister(1).setValue(xReg + 1);

                Register r1 = resource.getRegister(n1);
                Register sw = resource.getRegister(9);
                // > == < 순서
                if (xReg == r1.getValue())
                    sw.setValue(0x010);
                else if (xReg > r1.getValue())
                    sw.setValue(0x100);
                else
                    sw.setValue(0x001);
            }
            default -> throw new RuntimeException("Not implemented instruction : " + inst.getName());
        }
    }

    /**
     * @return 새로운 PC 값. 점프할 필요가 없다면 현재 PC값을 반환
     * @throws RuntimeException 프로그램이 종료될 경우(J @RETADR)
     */
    public int executeFormat3Inst(Instruction inst, String fullInst, int PC, boolean extended,
                                  ResourceManager resource) throws ProgramEndException, IOException {
        boolean direct = false, immediate = false, indirect = false;
        boolean x = false;

        int val = Integer.parseInt(fullInst.substring(0, 3), 16);
        // 0000 00ni xbpe
        if ((val & 0x30) == 0x30) {
            direct = true;
        } else if ((val & 0x10) == 0x10) {
            immediate = true;
        } else if ((val & 0x20) == 0x20) {
            indirect = true;
        }

        if ((val & 0x8) == 0x8) {
            x = true;
        }

        int displacement = Integer.parseInt(fullInst.substring(3), 16);
        // 음수로 변환
        int mask = (1 << ((extended ? 4 : 3) * 4)) - 1;
        if (extended && (displacement & 0x80000) == 0x80000) {
            displacement += 0xFFFFFFFF - mask;
        } else if (!extended && (displacement & 0x800) == 0x800) {
            displacement += 0xFFFFFFFF - mask;
        }

        // 항상 PC relative라고 가정
        int addr = immediate || extended ? displacement :
                convertDispToAddr(displacement, PC, resource.getMemory(), direct, indirect);

        switch (inst.getName()) {
            case "LDA" -> {
                int fetchedVal = immediate ? addr : resource.getMemory().getMemValue(addr, 3);
                resource.getRegister(0).setValue(fetchedVal);
            }
            case "LDT" -> {
                int fetchedVal = immediate ? addr : resource.getMemory().getMemValue(addr, 3);
                resource.getRegister(5).setValue(fetchedVal);
            }
            case "LDCH" -> {
                int xReg = resource.getRegister(1).getValue();
                int fetchedVal = immediate ? addr :
                        resource.getMemory().getMemValue(addr + (x ? xReg : 0), 1);
                resource.getRegister(0).setValue(fetchedVal);
            }
            case "STA" -> {
                int regValue = resource.getRegister(0).getValue();
                resource.getMemory().setMemValue(addr, 3, regValue);
            }
            case "STL" -> {
                int regValue = resource.getRegister(2).getValue();
                resource.getMemory().setMemValue(addr, 3, regValue);
            }
            case "STX" -> {
                int regValue = resource.getRegister(1).getValue();
                resource.getMemory().setMemValue(addr, 3, regValue);
            }
            case "STCH" -> {
                int regValue = resource.getRegister(0).getValue();
                int xReg = resource.getRegister(1).getValue();
                resource.getMemory().setMemValue(addr + (x ? xReg : 0), 1, regValue);
            }
            case "COMP" -> {
                int aReg = resource.getRegister(0).getValue();
                int fetchedVal = immediate ? addr : resource.getMemory().getMemValue(addr, 3);
                Register sw = resource.getRegister(9);
                // > == < 순서
                if (aReg == fetchedVal)
                    sw.setValue(0x010);
                else if (aReg > fetchedVal)
                    sw.setValue(0x100);
                else
                    sw.setValue(0x001);
            }
            case "J" -> {
                if (indirect) {
                    // 끝났다는 의미. COPY 프로그램에서만 가능함
                    throw new ProgramEndException();
                }
                return addr;
            }
            case "JSUB" -> {
                resource.getRegister(2).setValue(PC); // L 레지스터에 PC 저장
                return addr;
            }
            case "JEQ" -> {
                int sw = resource.getRegister(9).getValue();
                if ((sw & 0x010) == 0x010)
                    return addr;
            }
            case "JLT" -> {
                int sw = resource.getRegister(9).getValue();
                if ((sw & 0x001) == 0x001)
                    return addr;
            }
            case "RSUB" -> {
                int lReg = resource.getRegister(2).getValue();
                return lReg;
            }
            case "TD" -> {
                // 항상 성공한다고 가정
                resource.getRegister(9).setValue(0);
            }
            case "RD" -> {
                int device = resource.getMemory().getMemValue(addr, 1);
                if (device == 0xF1) {
                    // input device
                    // 1바이트를 읽어서 ASCII로 변환해서 A 레지스터에 입력한다
                    int input = inputDevice.read();
                    if (input == -1) // EOF
                        input = 0;
                    resource.getRegister(0).setValue(input & 0xFF);
                    System.out.printf("input : %c\n", (char) input);
                }
            }
            case "WD" -> {
                int device = resource.getMemory().getMemValue(addr, 1);
                if (device == 0x05) {
                    // output device
                    // A 레지스터의 하위 1바이트를 ASCII로 변환해서 output device에 쓴다
                    int aReg = resource.getRegister(0).getValue() & 0xFF;
                    System.out.printf("output : %c\n", (char) aReg);
                    outputDevice.write(aReg);
                }
            }
            default -> throw new RuntimeException("Not implemented instruction : " + inst.getName());
        }
        return PC;
    }

    private int convertDispToAddr(int disp, int PC, Memory memory, boolean direct, boolean indirect) {
        if (direct)
            return disp + PC;

        if (indirect) {
            int addr = disp + PC;
            addr = memory.getMemValue(addr, 3);
            return addr;
        }
        throw new RuntimeException("illegal operation");
    }
}
