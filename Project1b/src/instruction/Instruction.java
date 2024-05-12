package instruction;

public class Instruction {
    /**
     * 기계어 목록 파일의 한 줄을 읽고, 이를 파싱하여 저장한다.
     *
     * @param line 기계어 목록 파일의 한 줄
     * @throws RuntimeException 잘못된 파일 형식
     */
    public Instruction(String line) throws RuntimeException {
        // _name, _opcode, _numberOfOperand, _format
        String[] list = line.split(" ");
        assert list.length == 4;
        name = list[0];
        opcode = Integer.parseInt(list[1], 16);
        numberOfOperand = Integer.parseInt(list[2]);
        format = Integer.parseInt(list[3]);
    }

    /**
     * 기계어 명칭을 반환한다.
     *
     * @return 기계어 명칭
     */
    public String getName() {
        return name;
    }

    /**
     * 기계어의 opcode를 반환한다.
     *
     * @return 기계어의 opcode
     */
    public int getOpcode() {
        return opcode;
    }

    /**
     * 기계어의 operand 개수를 반환한다.
     *
     * @return 기계어의 operand 개수
     */
    public int getNumberOfOperand() {
        return numberOfOperand;
    }

    /**
     * 기계어의 형식을 반환한다.
     *
     * @return 기계어의 형식. 2형식인 경우 피연산자를 레지스터로 갖는다고 가정한다.
     */
    public int getFormat() {
        return format;
    }

    private final String name;
    private final int opcode;
    private final int numberOfOperand;
    private final int format;
}