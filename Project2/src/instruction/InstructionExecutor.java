package instruction;

import resource.ResourceManager;

/**
 * 명령어에 따라 수행할 동작을 정의한 모듈
 * ResourceManager의 상태를 적절히 변경함
 */
public class InstructionExecutor {
    public void executeFormat2Inst(Instruction inst, String fullInst, ResourceManager resource) {

    }

    /**
     * @return 점프해야 하는 지점까지의 offset 반환. 점프할 필요 없다면 0 반환
     * 프로그램이 종료될 경우(J @RETADR) -1 반환
     */
    public int executeFormat3Inst(Instruction inst, String fullInst, int PC, ResourceManager resource) {
        return 0;
    }
}
