package token;

import java.util.List;
import java.util.Optional;

public class InstructionToken extends Token {
    /**
     * 소스 코드 한 줄에 해당하는 토큰을 초기화한다.
     *
     * @param input 소스 코드 한 줄에 해당하는 문자열
     * @throws RuntimeException 소스 코드 컴파일 오류
     */
    public InstructionToken(
            String label, String operator, List<String> operands,
            int address, int size) throws RuntimeException {
        super(address, size);

        if (label != null)
            this.label = Optional.of(label);
        if (operator != null) {
            if (operator.startsWith("+")) {
                nixbpe[0] = true;
                this.operator = Optional.of(operator.substring(1));
            } else {
                this.operator = Optional.of(operator);
            }
        }
        if (operands != null) {
            this.operands = Optional.of(operands);
            for (String opnd : operands) {
                if (opnd.equals("X")) {
                    nixbpe[3] = true;
                    continue;
                }
                if (opnd.contains("#")) {
                    nixbpe[4] = true;
                } else if (opnd.contains("@")) {
                    nixbpe[5] = true;
                } else {
                    nixbpe[4] = true;
                    nixbpe[5] = true;
                }
            }
        }
    }

    public void setPcRelative() {
        nixbpe[2] = true;
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
//    @Override
//    public String toString() {
//        String label = _label.orElse("(no label)");
//        String operator = (isE() ? "+ " : "") + _operator.orElse("(no operator)");
//        String operand = (isN() ? "@" : "") + (isI() ? "#" : "")
//                + (_operands.isEmpty() ? "(no operand)" : _operands.stream().collect(Collectors.joining("/")))
//                + (isX() ? (_operands.isEmpty() ? "X" : "/X") : "");
//        String comment = _comment.orElse("(no comment)");
//        return label + '\t' + operator + '\t' + operand + '\t' + comment;
//    }
    public Optional<String> getLabel() {
        return label;
    }

    public Optional<String> getOperator() {
        return operator;
    }

    public Optional<List<String>> getOperands() {
        return operands;
    }

    public Optional<String> getComment() {
        return comment;
    }

    /**
     * nixbpe[0] == e bit
     * nixbpe[1] == p bit
     * ...
     * nixbpe[5[ == n bit
     */
    private final boolean[] nixbpe = new boolean[6];
    private Optional<String> label = Optional.empty();
    private Optional<String> operator = Optional.empty();
    private Optional<List<String>> operands = Optional.empty();
    private Optional<String> comment = Optional.empty();
}
