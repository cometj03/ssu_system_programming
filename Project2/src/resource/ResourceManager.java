package resource;

import java.util.ArrayList;
import java.util.List;

/**
 * SIC/XE 가상 머신의 메모리, 레지스터 값 등의 하드웨어 상태를 관리하는 모듈
 * <br/>
 * SIC/XE 머신은 실제 물리적인 하드웨어가 아니므로, 시뮬레이터를 구동시키기
 * 위한 가상의 하드웨어 장치
 * - 메모리 영역
 * - 레지스터 영역
 */
public class ResourceManager {
    private final Memory memory = new Memory();
    private final List<Register> registers = new ArrayList<>(10);

    public ResourceManager() {
        for (int i = 0; i < 10; i++)
            registers.add(new Register());
    }

    public Memory getMemory() {
        return this.memory;
    }

    public Register getRegister(int reg) throws RuntimeException {
        return switch (reg) {
            case 0 -> registers.get(0);
            case 1 -> registers.get(1);
            case 2 -> registers.get(2);
            case 3 -> registers.get(3);
            case 4 -> registers.get(4);
            case 5 -> registers.get(5);
            case 6 -> registers.get(6);
            case 8 -> registers.get(8);
            case 9 -> registers.get(9);
            default -> throw new RuntimeException("Wrong register number : " + reg);
        };
    }

    // test
    public static void main(String[] args) {
        ResourceManager resource = new ResourceManager();
        resource.getRegister(0).setValue(10);
        resource.getRegister(0).add(32);
        System.out.println(resource.getRegister(0).getValue()); // 42
    }
}
