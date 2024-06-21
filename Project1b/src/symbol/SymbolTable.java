package symbol;

import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

public class SymbolTable {

    public SymbolTable() {
        symbolMap = new HashMap<>();
    }

    /**
     * EQU를 제외한 명령어/지시어에 label이 포함되어 있는 경우, 해당 label을 심볼 테이블에 추가한다.
     *
     * @param label   라벨
     * @param address 심볼의 주소
     * @throws RuntimeException label의 길이가 6 초과이거나, 중복되어 있을 경우
     */
    public void putLabel(String label, int address) throws RuntimeException {
        if (label.length() > 6)
            throw new RuntimeException("label's length is too long: must be <= 6");
        if (symbolMap.containsKey(label))
            throw new RuntimeException("duplicated symbol");

        Symbol symbol = new Symbol(label, address, csectName.orElse(null));
        symbolMap.put(label, symbol);
    }

    /**
     * EQU에 label이 포함되어 있는 경우, 해당 label을 심볼 테이블에 추가한다.
     *
     * @param label      라벨
     * @param locctr     locctr 값
     * @param expression 수식
     * @throws RuntimeException expression 파싱 오류
     */
    public void putLabel(String label, int locctr, String expression) throws RuntimeException {
        if (expression.contains("*")) {
            putLabel(label, locctr);
            return;
        }

        // expression 계산
        String[] terms = expression.split("[-+]");
        char[] ops = new char[terms.length];
        for (int i = 0, t = 1; i < expression.length(); i++) {
            if (expression.charAt(i) == '-' || expression.charAt(i) == '+') {
                ops[t] = expression.charAt(i);
                t++;
            }
        }
        int value = 0;
        for (int t = 0; t < terms.length; t++) {
            Optional<Symbol> sym = searchSymbol(terms[t]);
            if (sym.isEmpty()) throw new RuntimeException("symbol " + terms[t] + " is not exist.");
            if (ops[t] == '-') {
                value -= sym.get().getAddress();
            } else {
                value += sym.get().getAddress();
            }
        }
        putLabel(label, value);
    }

    /**
     * EXTREF에 operand가 포함되어 있는 경우, 해당 operand를 심볼 테이블에 추가한다.
     *
     * @param refer operand에 적힌 하나의 심볼
     * @throws RuntimeException refer의 길이가 6 초과이거나, 중복되어 있을 경우
     */
    public void putRefer(String refer) throws RuntimeException {
        if (refer.length() > 6)
            throw new RuntimeException("label's length is too long: must be <= 6");
        if (symbolMap.containsKey(refer))
            throw new RuntimeException("duplicated symbol");
        symbolMap.put(refer, new Symbol(refer, true));
    }

    /**
     * 심볼 테이블에서 심볼을 찾는다.
     *
     * @param name 찾을 심볼 명칭
     * @return 심볼. 없을 경우 empty
     */
    public Optional<Symbol> searchSymbol(String name) {
        if (symbolMap.containsKey(name)) {
            return Optional.ofNullable(symbolMap.get(name));
        }
        return Optional.empty();
    }

    /**
     * 심볼 테이블에서 심볼을 찾아, 해당 심볼의 주소를 반환한다.
     *
     * @param symbolName 찾을 심볼 명칭
     * @return 심볼의 주소. 없을 경우 empty
     */
    public Optional<Integer> getAddress(String symbolName) {
        Optional<Symbol> optSymbol = searchSymbol(symbolName);
        return optSymbol.map(s -> s.getAddress());
    }

    /**
     * 심볼 테이블을 String으로 변환한다. Assembler.java에서 심볼 테이블을 출력하기 위해 사용한다.
     */
    @Override
    public String toString() {
        return symbolMap.values().stream()
                .map(sym -> sym.toString())
                .collect(Collectors.joining("\n"));
    }

    /**
     * 심볼 테이블. key: 심볼 명칭, value: 심볼 객체
     */
    private final HashMap<String, Symbol> symbolMap;
    private Optional<String> csectName = Optional.empty();

    public void setCsectName(String csectName) {
        this.csectName = Optional.of(csectName);
    }

    public Optional<String> getCsectName() {
        return csectName;
    }
}
