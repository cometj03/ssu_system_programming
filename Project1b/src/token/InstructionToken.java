package token;

import instruction.Instruction;
import instruction.operand.Register;
import literal.Literal;
import literal.LiteralTable;
import symbol.Symbol;
import symbol.SymbolTable;

import java.util.List;
import java.util.Optional;

public class InstructionToken extends Token {

    public static class TextInfo {
        private final int opcode;
        private final int nixbpe;
        private final int displacement;
        private final int sizeHalfBytes;

        TextInfo(int opcode, byte nixbpe, int displacement, int sizeHalfBytes) {
            this.opcode = opcode & 0x3F;
            this.nixbpe = nixbpe & 0xFF;
            this.displacement = displacement;
            this.sizeHalfBytes = sizeHalfBytes;
        }

        public String generateText() throws RuntimeException {
            switch (sizeHalfBytes) {
                case 2 -> {
                    return String.format("%02X", opcode);
                }
                case 4 -> {
                    return String.format("%04X", (opcode << 8) | (displacement & 0xF));
                }
                case 6 -> {
                    return String.format("%06X", (opcode << 18) | (nixbpe << 12) | (displacement & 0xFFF));
                }
                case 8 -> {
                    return String.format("%08X", (opcode << 26) | (nixbpe << 20) | (displacement & 0xFFFFF));
                }
                default -> throw new RuntimeException("illegal text size : " + sizeHalfBytes);
            }
        }
    }

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

    public TextInfo getTextInfo(SymbolTable symbolTable, LiteralTable literalTable) throws RuntimeException {
        int PC = getAddress() + getSize();
        int opcode = instruction.getOpcode() & 0x3F;

        // 3형식인 명령어는 피연산자 하나만 가짐 (immediate, symbol, indirect)
        if (instruction.getFormat() == 3) {
            if (operands.isEmpty())
                throw new RuntimeException("missing operands\n" +
                        "(no operands)\t" + getTokenString());
            String operand = operands.get(0);

            int addr = 0;
            if (isI()) {
                // immediate 일 때
                try {
                    addr = Integer.parseInt(operand.substring(1));
                } catch (NumberFormatException e) {
                    throw new RuntimeException("failed to parse int : " + operand +
                            "\n(immediate operand parse error)\t" + getTokenString());
                }
            } else if (operand.startsWith("=")) {
                // 리터럴일 때
                Optional<Literal> literal = literalTable.searchLiteral(operand);
                if (literal.isEmpty())
                    throw new RuntimeException("missing literal definition\n" +
                            "(no literal)\t" + getTokenString());
                if (literal.get().getAddress().isEmpty())
                    throw new RuntimeException("literal " + literal.get().getLiteral() + "'s address is not defined yet.");
                addr = literal.get().getAddress().get() - PC;
            } else {
                // symbol, indirect 일 때
                if (isN()) operand = operand.substring(1);
                Optional<Symbol> symbol = symbolTable.searchSymbol(operand);
                if (instruction.getNumberOfOperand() > 0 && symbol.isEmpty())
                    throw new RuntimeException("missing symbol definition\n" + operand +
                            "(no label)\t" + getTokenString());

                if (instruction.getNumberOfOperand() == 0) {
                    addr = 0;
                    // do nothing
                } else if (symbol.get().isReference()) {
                    // reference이면 addr 부분은 0
                    addr = 0;
                    // TODO modification
                } else {
                    addr = symbol.get().getAddress() - PC;
                }
            }
            return new TextInfo(opcode, getNixbpe(), addr, isE() ? 8 : 6);
        }

        // 2형식인 경우
        // 피연산자가 레지스터 1개 또는 2개이다
        if (instruction.getFormat() == 2) {
            List<Register> regOps = operands.stream()
                    .map(x -> {
                        try {
                            return Register.stringToRegister(x);
                        } catch (RuntimeException e) {
                            throw new RuntimeException(e.getMessage() +
                                    "\n(illegal reg operand)\t" + getTokenString());
                        }
                    }).toList();
            int requiredOperandCnt = instruction.getNumberOfOperand();
            if (regOps.size() > 2 || regOps.size() != requiredOperandCnt)
                throw new RuntimeException(requiredOperandCnt + " operand(s) are required. detected : " + regOps.size() +
                        "\n(mismatch of reg operand count)\t" + getTokenString());

            int addr = 0;
            addr |= (regOps.get(0).getValue() & 0x3) << 2;
            if (requiredOperandCnt == 2) addr |= regOps.get(1).getValue() & 0x3;
            return new TextInfo(opcode, (byte) 0, addr, 4);
        }
        // 1형식인 경우
        return new TextInfo(opcode, (byte) 0, 0, 2);
    }

    public byte getNixbpe() {
        int ret = 0;
        for (int i = 0; i < 6; i++)
            if (nixbpe[i]) ret |= (1 << i);
        return (byte) ret;
    }

    public boolean isN() {
        return nixbpe[5];
    }

    public boolean isI() {
        return nixbpe[4];
    }

    public boolean isX() {
        return nixbpe[3];
    }

    /*
     * Base relative는 구현하지 않음.
     * public boolean isB() {
     * return false;
     * }
     */

    public boolean isP() {
        return nixbpe[1];
    }

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
