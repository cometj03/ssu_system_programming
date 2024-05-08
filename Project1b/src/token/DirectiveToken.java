package token;

import directive.Directive;

public class DirectiveToken extends Token {
    // directive 타입?

    public DirectiveToken(Directive directive, int address, int size) {
        super(address, size);
        this.directiveType = directive;
    }

    private final Directive directiveType;
}
