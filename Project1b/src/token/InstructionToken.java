package token;

import instruction.Instruction;

import java.util.List;

public class InstructionToken extends Token {

    public InstructionToken(
            Instruction instruction,
            List<String> operands,
            boolean nBit, boolean iBit, boolean xBit, boolean pBit, boolean eBit,
            String tokenString,
            int address,
            int size) throws RuntimeException {
        super(tokenString, address, size);
        this.operands = operands;
        this.instruction = instruction;
        nixbpe[0] = eBit;
        nixbpe[1] = pBit;
//        nixbpe[2] = bBit;
        nixbpe[3] = xBit;
        nixbpe[4] = iBit;
        nixbpe[5] = nBit;
    }

    public byte getNixbpe() {
        int ret = 0;
        for (int i = 0; i < 6; i++)
            if (nixbpe[i]) ret |= (1 << i);
        return (byte) ret;
    }

    /**
     * 토큰의 iNdirect bit가 1인지 여부를 반환한다.
     *
     * @return N bit가 1인지 여부
     */
    public boolean isN() {
        return nixbpe[5];
    }

    /**
     * 토큰의 Immediate bit가 1인지 여부를 반환한다.
     *
     * @return I bit가 1인지 여부
     */
    public boolean isI() {
        return nixbpe[4];
    }

    /**
     * 토큰의 indeX bit가 1인지 여부를 반환한다.
     *
     * @return X bit가 1인지 여부
     */
    public boolean isX() {
        return nixbpe[3];
    }

    /*
     * Base relative는 구현하지 않음.
     * public boolean isB() {
     * return false;
     * }
     */

    /**
     * 토큰의 Pc relative bit가 1인지 여부를 반환한다.
     *
     * @return P bit가 1인지 여부
     */
    public boolean isP() {
        return nixbpe[1];
    }

    /**
     * 토큰의 Extra bit가 1인지 여부를 반환한다.
     *
     * @return E bit가 1인지 여부
     */
    public boolean isE() {
        return nixbpe[0];
    }

    /**
     * 토큰을 String으로 변환한다. 원활한 디버깅을 위해 기본적으로 제공한 함수이며, Assembler.java에서는 해당 함수를 사용하지
     * 않으므로 자유롭게 변경하여 사용한다.
     * 아래 함수는 피연산자에 X가 지정되었더라도 _operands는 X를 저장하지 않고 X bit만 1로 변경한 상태를 가정하였다.
     */
    @Override
    public String toString() {
        String operator = (isE() ? "+ " : "") + this.instruction.getName();
        String operand = (isN() ? "@" : "") + (isI() ? "#" : "")
                + (operands.isEmpty() ? "(no operand)" : String.join("/", operands))
                + (isX() ? (operands.isEmpty() ? "X" : "/X") : "");
        return operator + '\t' + operand;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public List<String> getOperands() {
        return operands;
    }

    /**
     * nixbpe[0] == e bit
     * nixbpe[1] == p bit
     * ...
     * nixbpe[5[ == n bit
     */
    private final boolean[] nixbpe = new boolean[6];
    private final Instruction instruction;
    private final List<String> operands;
}
