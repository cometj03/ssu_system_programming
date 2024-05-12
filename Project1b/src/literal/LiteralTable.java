package literal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class LiteralTable {

    public LiteralTable() {
        literalMap = new HashMap<>();
    }

    /**
     * 리터럴을 리터럴 테이블에 추가한다.
     *
     * @param literalStr 추가할 리터럴
     * @throws RuntimeException 비정상적인 리터럴 서식
     */
    public void putLiteral(String literalStr) throws RuntimeException {
        if (!literalStr.startsWith("="))
            throw new RuntimeException("wrong literal format : " + literalStr);

        literalMap.put(literalStr, new Literal(literalStr));
        unresolvedLiterals.add(literalStr);
    }

    /**
     * @param startLocctr 시작 주소
     * @return 여기서 주소가 결정된 리터럴 리스트
     */
    public List<Literal> resolveLiteralAddress(int startLocctr) throws RuntimeException {
        List<Literal> newLiterals = new ArrayList<>();

        for (String litKey : unresolvedLiterals) {
            Literal lit = literalMap.get(litKey);
            if (lit != null) newLiterals.add(lit);
            else
                throw new RuntimeException("unknown literal : " + litKey);
            lit.setAddress(startLocctr);
            startLocctr += lit.getSize();
        }
        unresolvedLiterals.clear();
        return newLiterals;
    }

    public Optional<Literal> searchLiteral(String str) {
        if (literalMap.containsKey(str)) {
            return Optional.ofNullable(literalMap.get(str));
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        literalMap.forEach((k, v) -> builder.append(v.toString()).append("\n"));
        return builder.toString();
    }

    /**
     * 리터럴 맵. key: 리터럴 String, value: 리터럴 객체
     */
    private final HashMap<String, Literal> literalMap;
    private final List<String> unresolvedLiterals = new ArrayList<>();
}
