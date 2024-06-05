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
    public int PC = 0;

    private final ResourceManager resource;
    private final Loader loader;
    private final InstructionExecutor executor;
    private final InstructionTable instructionTable;

    public Simulator() throws IOException {
        this.resource = new ResourceManager();
        this.loader = new Loader();
        this.executor = new InstructionExecutor();
        this.instructionTable = new InstructionTable("inst_table.txt");
    }

    public Loader.LoaderInfo load(File objFile, int programStartAddr) throws IOException {
        Loader.LoaderInfo loaderInfo = loader.loadFromFile(resource.getMemory(), objFile, programStartAddr);
        PC = loaderInfo.programStartAddr;
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
        int opcode = resource.getMemory().getMemValue(PC, 1) & 0xFC; // 상위 6비트
        Optional<Instruction> inst = instructionTable.search(opcode);
        if (inst.isEmpty())
            throw new RuntimeException(String.format("Unknown opcode : %X", opcode));

        switch (inst.get().getFormat()) {
            case 1 -> {
                // do nothing
                PC += 1;
            }
            case 2 -> {
                String fullInst = resource.getMemory().getMemString(PC, 2);
                PC += 2;
                executor.executeFormat2Inst(inst.get(), fullInst, resource);
            }
            case 3 -> {
                int val = resource.getMemory().getMemValue(PC, 2);

                String fullInst;
                if ((val & (1 << 4)) != 0) {
                    // extended
                    fullInst = resource.getMemory().getMemString(PC, 4);
                    PC += 4;
                } else {
                    fullInst = resource.getMemory().getMemString(PC, 3);
                    PC += 3;
                }
                // 점프해야 하는 경우 해당 offset을 반환
                int offset = executor.executeFormat3Inst(inst.get(), fullInst, PC, resource);
                if (offset < 0) return null;
            }
        }
        return inst.get().getName();
    }
}
