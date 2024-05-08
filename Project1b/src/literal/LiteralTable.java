package literal;

import java.util.HashMap;
import java.util.Optional;

public class LiteralTable {
    /**
     * 리터럴 테이블을 초기화한다.
     */
    public LiteralTable() {
        literalMap = new HashMap<String, Literal>();
    }

    /**
     * 리터럴을 리터럴 테이블에 추가한다.
     *
     * @param literal 추가할 리터럴
     * @throws RuntimeException 비정상적인 리터럴 서식
     */
    public void putLiteral(String literal) throws RuntimeException {
        // TODO: 리터럴을 literalMap에 추가하기.
    }

    // TODO: 추가로 필요한 method 구현하기.

    /**
     * 리터럴 테이블을 String으로 변환한다.
     */
    @Override
    public String toString() {
        // TODO: 구현하기. literal.Literal 객체의 toString을 활용하자.
        return "<literal.LiteralTable.toString()>";
    }

    /**
     * 리터럴 맵. key: 리터럴 String, value: 리터럴 객체
     */
    private HashMap<String, Literal> literalMap;
}

class Literal {
    /**
     * 리터럴 객체를 초기화한다.
     *
     * @param literal 리터럴 String
     */
    public Literal(String literal) {
        // TODO: 리터럴 객체 초기화.
    }

    /**
     * 리터럴 String을 반환한다.
     *
     * @return 리터럴 String
     */
    public String getLiteral() {
        return _literal;
    }

    /**
     * 리터럴의 주소를 반환한다. 주소가 지정되지 않은 경우, Optional.empty()를 반환한다.
     *
     * @return 리터럴의 주소
     */
    public Optional<Integer> getAddress() {
        return _address;
    }

    // TODO: 추가로 선언한 field에 대한 getter 작성하기.

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
    private String _literal;

    /**
     * 리터럴 주소. 주소가 지정되지 않은 경우 empty
     */
    private Optional<Integer> _address;

    // TODO: 추가로 필요한 field 선언하기.
}