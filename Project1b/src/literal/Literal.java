package literal;

import java.util.Optional;

public abstract class Literal {
    public Literal(String literal) {
        this.literal = literal;
    }

    public String getLiteral() {
        return literal;
    }

    public Optional<Integer> getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = Optional.of(address);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "<literal.Literal.toString()>";
    }

    private final String literal;
    /**
     * 리터럴 주소. 주소가 지정되지 않은 경우 empty
     */
    private Optional<Integer> address = Optional.empty();
    private int size;
}
