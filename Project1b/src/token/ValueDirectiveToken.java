package token;

import directive.Directive;
import numeric.Numeric;

import java.util.ArrayList;
import java.util.List;

// WORD, BYTE, LTORG(리터럴)
public class ValueDirectiveToken extends DirectiveToken {
    public ValueDirectiveToken(Directive directive, List<Numeric> values,
                               String tokenString, int address, int size) {
        super(directive, tokenString, address, size);
        this.values = values;
    }

    public ValueDirectiveToken(Directive directive, Numeric value,
                               String tokenString, int address, int size) {
        this(directive, new ArrayList<>(), tokenString, address, size);
        this.values.add(value);
    }

    private final List<Numeric> values;
}