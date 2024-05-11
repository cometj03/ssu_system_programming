package literal;

import numeric.Numeric;

public class NumericLiteral extends Literal {

    // Literal로 그냥 통합하기
    public NumericLiteral(String literal) throws RuntimeException {
        super(literal);
        numeric = new Numeric(literal);
        setSize(numeric.getSize());
    }

    private final Numeric numeric;
}
