package symbol;

public class Symbol {
    public Symbol(String name, int address, String definedCsect) {
        this(name, address, false, definedCsect);
    }

    public Symbol(String name, int address, boolean isRef) {
        this(name, address, isRef, null);
    }

    public Symbol(String name, int address, boolean isRef, String definedCsect) {
        this.name = name;
        this.address = address;
        this.ref = isRef;
        this.definedCsect = definedCsect;
    }

    public String getName() {
        return name;
    }

    public int getAddress() {
        return address;
    }

    public boolean isReference() {
        return ref;
    }

    @Override
    public String toString() {
        String addr = ref ? "REF" : String.format("%#04X", address);
        return name + "\t" + addr + "\t";
    }

    private final String definedCsect; // symbol이 정의된 control section 이름
    private final String name;
    private final int address;
    private boolean ref;
}
