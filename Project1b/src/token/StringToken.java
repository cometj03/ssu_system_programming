package token;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StringToken {
    public StringToken(String tokenString) throws RuntimeException {
        this.tokenString = tokenString;
        if (tokenString.startsWith(".")) {
            comment = Optional.of(tokenString);
            return;
        }
        String[] tok = tokenString.split("\t");

        if (!tok[0].isEmpty())
            label = Optional.of(tok[0]);

        if (tok.length >= 2 && !tok[1].isEmpty()) {
            operator = Optional.of(tok[1]);
        }

        if (tok.length >= 3) {
            for (String opnd : tok[2].split(","))
                operands.add(opnd);
        }

        if (tok.length >= 4 && !tok[3].isEmpty())
            comment = Optional.of(tok[3]);
    }

    public String getTokenString() {
        return tokenString;
    }

    public Optional<String> getLabel() {
        return label;
    }

    public Optional<String> getOperator() {
        return operator;
    }

    public List<String> getOperands() {
        return operands;
    }

    public Optional<String> getComment() {
        return comment;
    }

    private final String tokenString;
    private Optional<String> label = Optional.empty();
    private Optional<String> operator = Optional.empty();
    private List<String> operands = new ArrayList<>();
    private Optional<String> comment = Optional.empty();
}
