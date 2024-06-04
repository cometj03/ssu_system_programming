package simulator;

import instruction.InstructionExecutor;
import instruction.InstructionTable;
import loader.Loader;
import resource.ResourceManager;

import java.io.IOException;
import java.util.Optional;

/**
 * 실질적인 SIC/XE 시뮬레이터 동작을 수행하고, 그 결과를 반환하는 모듈.
 * - object code 로드 동작 수행
 * - 명령어 실행 동작 수행
 */
public class Simulator {
    public Optional<String> programName = Optional.empty();

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

    public void load(String objFileName, int programStartAddr) throws IOException {
        String programName = loader.loadFromFile(resource.getMemory(), objFileName, programStartAddr);
        this.programName = Optional.ofNullable(programName);
    }
}
