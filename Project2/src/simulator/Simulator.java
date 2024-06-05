package simulator;

import instruction.InstructionExecutor;
import instruction.InstructionTable;
import loader.Loader;
import resource.Memory;
import resource.Register;
import resource.ResourceManager;

import java.io.File;
import java.io.IOException;

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

    public Simulator() throws IOException {
        this.resource = new ResourceManager();
        this.loader = new Loader();
        this.executor = new InstructionExecutor();
        this.instructionTable = new InstructionTable("inst_table.txt");
    }

    public Loader.LoaderInfo load(File objFile, int programStartAddr) throws IOException {
        return loader.loadFromFile(resource.getMemory(), objFile, programStartAddr);
    }

    public Memory getMemory() {
        return resource.getMemory();
    }

    public Register getRegister(int reg) {
        return resource.getRegister(reg);
    }
}
