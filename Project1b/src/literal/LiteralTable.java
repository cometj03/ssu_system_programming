package literal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        if (!literal.startsWith("="))
            throw new RuntimeException("wrong literal format : " + literal);

        Literal lit;
        if (literal.startsWith("=C")) {
            lit = new CharLiteral(literal);
        } else {
            lit = new NumericLiteral(literal);
        }
        literalMap.put(literal, lit);
        unresolvedLiterals.add(literal);
    }

    public void resolveLiteralAddress(int startLocctr) {
        for (String litKey : unresolvedLiterals) {
            // todo
        }

        unresolvedLiterals.clear();
    }

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
    private final HashMap<String, Literal> literalMap;
    private final List<String> unresolvedLiterals = new ArrayList<>();
}
