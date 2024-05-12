package token;

import directive.Directive;

import java.util.List;
import java.util.Optional;

public class DirectiveToken extends Token {

    public DirectiveToken(Directive directive,
                          String tokenString, int address, int size) {
        super(tokenString, address, size);
        this.directiveType = directive;
        this.operands = Optional.empty();
    }

    public DirectiveToken(Directive directive, List<String> operands,
                          String tokenString, int address, int size) {
        this(directive, tokenString, address, size);
        this.operands = Optional.of(operands);
    }

    public Directive getDirectiveType() {
        return directiveType;
    }

    public Optional<List<String>> getOperands() {
        return operands;
    }

    private final Directive directiveType;
    private Optional<List<String>> operands; // EXTDEF, EXTREF에서 필요함
}


