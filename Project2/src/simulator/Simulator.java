package simulator;

import instruction.InstructionExecutor;
import loader.Loader;
import resource.ResourceManager;

/**
 * 실질적인 SIC/XE 시뮬레이터 동작을 수행하고, 그 결과를 반환하는 모듈.
 * - object code 로드 동작 수행
 * - 명령어 실행 동작 수행
 */
public class Simulator {
    private final ResourceManager resource;
    private final Loader loader;
    private final InstructionExecutor executor;

    public Simulator() {
        this.resource = new ResourceManager();
        this.loader = new Loader();
        this.executor = new InstructionExecutor();
    }

    public ResourceManager getResource() {
        return resource;
    }
}
