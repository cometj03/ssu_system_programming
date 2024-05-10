package literal;

import java.util.Optional;

public abstract class Literal {
    /**
     * 리터럴 객체를 초기화한다.
     *
     * @param literal 리터럴 String
     */
    public Literal(String literal) {
        this.literal = literal;
    }

    /**
     * 리터럴 String을 반환한다.
     *
     * @return 리터럴 String
     */
    public String getLiteral() {
        return literal;
    }

    /**
     * 리터럴의 주소를 반환한다. 주소가 지정되지 않은 경우, Optional.empty()를 반환한다.
     *
     * @return 리터럴의 주소
     */
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

    /**
     * 리터럴을 String으로 변환한다. 리터럴의 address에 관한 정보도 리턴값에 포함되어야 한다.
     */
    @Override
    public String toString() {
        // TODO: 리터럴을 String으로 표현하기.
        return "<literal.Literal.toString()>";
    }

    /**
     * 리터럴 String
     */
    private final String literal;

    /**
     * 리터럴 주소. 주소가 지정되지 않은 경우 empty
     */
    private Optional<Integer> address = Optional.empty();

    private int size;
}
