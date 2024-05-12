package literal;

import numeric.Numeric;

import java.util.Optional;

public class Literal {
    public Literal(String literal) {
        this.literal = literal;
        this.numeric = new Numeric(literal);
    }

    @Override
    public String toString() throws RuntimeException {
        if (address.isEmpty())
            throw new RuntimeException("Literal.toString(): " + literal + "'s address is not defined yet.");
        return literal + "\t" + String.format("0x%04X", address.get());
    }

    /**
     * 리터럴 주소. 주소가 지정되지 않은 경우 empty
     */
    private Optional<Integer> address = Optional.empty();
    private final String literal;
    private final Numeric numeric;

    /**
     * get set
     */

    public Optional<Integer> getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = Optional.of(address);
    }

    public String getLiteral() {
        return literal;
    }

    public int getSize() {
        return numeric.getSize();
    }

    public Numeric getNumeric() {
        return numeric;
    }
}
