package symbol;

import java.util.Optional;

public class Symbol {

    /**
     * @param name         symbol의 이름
     * @param address      symbol이 정의된 주소
     * @param isRef        reference record에서 선언되었는지 여부
     * @param definedCsect 정의된 control section의 이름 (nullable)
     */
    public Symbol(String name, int address, String definedCsect, boolean isRef) {
        this.name = name;
        this.address = address;
        this.ref = isRef;
        this.definedCsect = Optional.ofNullable(definedCsect);
    }

    public Symbol(String name, int address, String definedCsect) {
        this(name, address, definedCsect, false);
    }

    public Symbol(String name, boolean isRef) {
        this(name, -1, null, isRef);
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
        String addr = ref ? "REF" : String.format("0x%04X", address);
        String csect = (!ref && definedCsect.isPresent()) ? definedCsect.get() : "";
        return name + "\t" + addr + "\t" + csect;
    }

    private final Optional<String> definedCsect; // symbol이 정의된 control section 이름
    private final String name;
    private final int address;
    private final boolean ref;
}
