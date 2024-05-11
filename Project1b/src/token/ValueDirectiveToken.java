package token;

import directive.Directive;
import numeric.Numeric;

import java.util.List;

// WORD, BYTE, LTORG(리터럴)
public class ValueDirectiveToken extends DirectiveToken {
    public ValueDirectiveToken(Directive directive, List<Numeric> numerics,
                               String tokenString, int address, int size) {
        super(directive, tokenString, address, size);
        this.numerics = numerics;
    }

    public ValueDirectiveToken(Directive directive, Numeric numeric,
                               String tokenString, int address, int size) {
        this(directive, List.of(numeric), tokenString, address, size);
    }

    private final List<Numeric> numerics;
}