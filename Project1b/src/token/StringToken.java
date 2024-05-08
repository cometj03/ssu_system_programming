package token;

import java.util.ArrayList;
import java.util.Optional;

public class StringToken {
    public StringToken(String tokenString) throws RuntimeException {
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

    public Optional<String> getLabel() {
        return label;
    }

    public Optional<String> getOperator() {
        return operator;
    }

    public ArrayList<String> getOperands() {
        return operands;
    }

    public Optional<String> getComment() {
        return comment;
    }

    private Optional<String> label = Optional.empty();
    private Optional<String> operator = Optional.empty();
    private ArrayList<String> operands = new ArrayList<>();
    private Optional<String> comment = Optional.empty();
}
