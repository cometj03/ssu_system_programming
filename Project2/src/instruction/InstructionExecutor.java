package instruction;

import resource.ResourceManager;

/**
 * 명령어에 따라 수행할 동작을 정의한 모듈
 * ResourceManager의 상태를 적절히 변경함
 */
public class InstructionExecutor {
    public void executeFormat2Inst(Instruction inst, String fullInst, ResourceManager resource) {
        int rest = Integer.parseInt(fullInst.substring(2, 4), 16);
        int r1 = (rest >> 4) & 0xF;
        int r2 = rest & 0xF;
        switch (inst.getName()) {
            case "ADDR" -> {
                // r2 <- (r2) + (r1)
            }
            case "DIVR" -> {
                // r2 <- (r2) / (r1)
            }
            case "MULR" -> {
                // r2 <- (r2) * (r1)
            }
            case "SUBR" -> {
                // r2 <- (r2) - (r1)
            }
            case "CLEAR" -> {
                // r1 <- 0
            }
            case "COMPR" -> {
                // (r1):(r2)
            }
            case "RMO" -> {
                // r2 <- r1
            }
            case "TIXR" -> {
                // X <- (X) + 1, (X):(r1)
            }
            default -> {
                // not implemented
            }
        }
    }

    /**
     * @return 점프해야 하는 지점까지의 offset 반환. 점프할 필요 없다면 0 반환
     * @throws 프로그램이 종료될 경우(J @RETADR)
     */
    public int executeFormat3Inst(Instruction inst, String fullInst, int PC, boolean extended,
                                  ResourceManager resource) throws RuntimeException {
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
        // PC relative라고 가정

        int displacement = Integer.parseInt(fullInst.substring(3), 16);
        // 음수로 변환
        if (extended) {
            if ((displacement & 0x80000) == 0x80000)
                displacement += 0xFFF00000;
        } else {
            if ((displacement & 0x800) == 0x800)
                displacement += 0xFFFFF000;
        }

        switch (inst.getName()) {

        }
        return 0;
    }
}
