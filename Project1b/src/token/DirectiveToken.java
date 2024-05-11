package token;

import directive.Directive;

public class DirectiveToken extends Token {

    public DirectiveToken(Directive directive,
                          String tokenString, int address, int size) {
        super(tokenString, address, size);
        this.directiveType = directive;
    }

    public Directive getDirectiveType() {
        return directiveType;
    }

    private final Directive directiveType;
}


