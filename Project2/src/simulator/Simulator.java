package simulator;

import instruction.Instruction;
import instruction.InstructionExecutor;
import instruction.InstructionTable;
import loader.Loader;
import resource.Memory;
import resource.Register;
import resource.ResourceManager;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * 실질적인 SIC/XE 시뮬레이터 동작을 수행하고, 그 결과를 반환하는 모듈.
 * - object code 로드 동작 수행
 * - 명령어 실행 동작 수행
 */
public class Simulator {
    private final ResourceManager resource;
    private final Loader loader;
    private final InstructionExecutor executor;
    private final InstructionTable instructionTable;

    public Simulator(String inputFileName, String outputFileName) throws IOException {
        this.resource = new ResourceManager();
        this.loader = new Loader();
        this.executor = new InstructionExecutor(inputFileName, outputFileName);
        this.instructionTable = new InstructionTable("inst_table.txt");
    }

    public int getPC() {
        return resource.getRegister(8).getValue();
    }

    private void setPC(int value) {
        resource.getRegister(8).setValue(value);
    }

    private void addPC(int value) {
        resource.getRegister(8).add(value);
    }

    public Loader.LoaderInfo load(File objFile, int programStartAddr) throws IOException {
        Loader.LoaderInfo loaderInfo = loader.loadFromFile(resource.getMemory(), objFile, programStartAddr);
        setPC(loaderInfo.programStartAddr);
        return loaderInfo;
    }

    public Memory getMemory() {
        return resource.getMemory();
    }

    public Register getRegister(int reg) {
        return resource.getRegister(reg);
    }

    /**
     * @return 실행한 명령어
     */
    public String executeSingleInst() {
        int opcode = resource.getMemory().getMemValue(getPC(), 1) & 0xFC; // 상위 6비트
        Optional<Instruction> inst = instructionTable.search(opcode);
        if (inst.isEmpty())
            throw new RuntimeException(String.format("Unknown opcode : %X", opcode));

        switch (inst.get().getFormat()) {
            case 1 -> {
                // do nothing
                addPC(1);
            }
            case 2 -> {
                String fullInst = resource.getMemory().getMemString(getPC(), 2);
                addPC(2);
                executor.executeFormat2Inst(inst.get(), fullInst, resource);
            }
            case 3 -> {
                int val = resource.getMemory().getMemValue(getPC(), 2);

                boolean extended = (val & 0x10) == 0x10;
                String fullInst = resource.getMemory().getMemString(getPC(), extended ? 4 : 3);
                addPC(extended ? 4 : 3);

                // 점프해야 하는 경우 해당 offset을 반환
                try {
                    int newPC = executor.executeFormat3Inst(inst.get(), fullInst, getPC(), extended, resource);
                    setPC(newPC);
                } catch (InstructionExecutor.ProgramEndException e) {
                    // 프로그램 종료시 예외 던짐
                    return null;
                } catch (IOException ioe) {
                    System.out.println("IOException at " + fullInst + " : \n" + ioe.getMessage());
                }
            }
        }
        return inst.get().getName();
    }
}
