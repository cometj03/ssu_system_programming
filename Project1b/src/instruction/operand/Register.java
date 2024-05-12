package instruction.operand;

public enum Register {
    A(0),
    X(1),
    L(2),
    B(3),
    S(4),
    T(5),
    F(6),
    PC(8),
    SW(9);

    private Register(int value) {
        this.value = value;
    }

    private final int value;

    public int getValue() {
        return value;
    }

    public static Register stringToRegister(String str) throws RuntimeException {
        return switch (str) {
            case "A" -> A;
            case "X" -> X;
            case "L" -> L;
            case "B" -> B;
            case "S" -> S;
            case "T" -> T;
            case "F" -> F;
            case "PC" -> PC;
            case "SW" -> SW;
            default -> throw new RuntimeException("illegal register name (" + str + ")");
        };
    }
}
