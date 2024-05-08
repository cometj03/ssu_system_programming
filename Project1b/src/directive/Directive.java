package directive;

public enum Directive {
    START,
    CSECT,
    EXTDEF,
    EXTREF,
    BYTE,
    WORD,
    RESB,
    RESW,
    LTORG,
    EQU,
    END;

    public static Directive fromString(String str) throws RuntimeException {
        return switch (str) {
            case "START" -> START;
            case "CSECT" -> CSECT;
            case "EXTDEF" -> EXTDEF;
            case "EXTREF" -> EXTREF;
            case "BYTE" -> BYTE;
            case "WORD" -> WORD;
            case "RESB" -> RESB;
            case "RESW" -> RESW;
            case "LTORG" -> LTORG;
            case "EQU" -> EQU;
            case "END" -> END;
            default -> throw new RuntimeException("illegal directive name (" + str + ")");
        };
    }
}
