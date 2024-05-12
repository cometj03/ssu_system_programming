import directive.Directive;
import instruction.Instruction;
import instruction.InstructionTable;
import literal.Literal;
import literal.LiteralTable;
import numeric.Numeric;
import symbol.Symbol;
import symbol.SymbolTable;
import token.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ControlSection {
    /**
     * pass1 작업을 수행한다. 기계어 목록 테이블을 통해 소스 코드를 토큰화하고, 심볼 테이블 및 리터럴 테이블을 초기화환다.
     *
     * @param instTable 기계어 목록 테이블
     * @param input     하나의 control section에 속하는 소스 코드. 마지막 줄은 END directive를 강제로
     *                  추가하였음.
     * @throws RuntimeException 소스 코드 컴파일 오류
     */
    public ControlSection(InstructionTable instTable, ArrayList<String> input) throws RuntimeException {
        this.instTable = instTable;
        this.tokens = new ArrayList<>();
        this.symbolTable = new SymbolTable();
        this.literalTable = new LiteralTable();

        List<StringToken> stringTokens = input.stream().map(x -> new StringToken(x)).toList();

        for (StringToken stringToken : stringTokens) {
            if (stringToken.getOperator().isEmpty()) {
                continue;
            }

            String operator = stringToken.getOperator().get();
            Optional<Instruction> inst = instTable.search(operator);

            Token token;
            if (inst.isPresent()) {
                // 명령어 수행
                token = handlePass1Instruction(stringToken, inst.get());
            } else {
                // 지시어 수행
                token = handlePass1Directive(stringToken);
            }
            locctr += token.getSize();
            tokens.add(token);
            System.out.println(token.getAddress() + "\t" + token.getTokenString()); // 디버깅 용
        }
    }

    private InstructionToken handlePass1Instruction(
            StringToken stringToken,
            Instruction instruction) throws RuntimeException {
        List<String> operands = new ArrayList<>();
        int size = instruction.getFormat();
        boolean nBit, iBit, xBit, pBit, eBit;
        nBit = iBit = xBit = pBit = eBit = false;

        if (stringToken.getOperator().get().startsWith("+")) {
            ++size;
            eBit = true;
        }

        if (stringToken.getLabel().isPresent()) {
            String label = stringToken.getLabel().get();
            symbolTable.putLabel(label, locctr);
        }

        // TODO: operand 구분 확실히 -> Operand 객체로 변환
        for (String opnd : stringToken.getOperands()) {
            if (opnd.equals("X")) {
                xBit = true;
                continue;
            }
            operands.add(opnd);
            if (opnd.startsWith("=")) {
                literalTable.putLiteral(opnd);
                pBit = true;
                continue;
            }
            if (opnd.startsWith("#")) {
                iBit = true;
                continue;
            }
            if (opnd.startsWith("@")) {
                nBit = true;
                continue;
            }

            Optional<Symbol> sym = symbolTable.searchSymbol(opnd);
            if (sym.isPresent() && !sym.get().isReference()) {
                pBit = true;
                eBit = true;
            }
        }

        return new InstructionToken(instruction, operands,
                nBit, iBit, xBit, pBit, eBit, stringToken.getTokenString(), locctr, size);
    }

    private DirectiveToken handlePass1Directive(StringToken stringToken) throws RuntimeException {
        String tokenString = stringToken.getTokenString();
        String operator = stringToken.getOperator().get();
        Directive directive = Directive.fromString(operator);
        Optional<String> label = stringToken.getLabel();

        // label check
        switch (directive) {
            case START, CSECT, BYTE, WORD, RESB, RESW, EQU:
                if (label.isEmpty()) throw new RuntimeException("no label with " + operator);
        }

        switch (directive) {
            case START, CSECT -> {
                if (!stringToken.getOperands().isEmpty()) {
                    locctr = Integer.parseInt(stringToken.getOperands().get(0));
                }
                symbolTable.putLabel(label.get(), locctr);
                symbolTable.setCsectName(label.get());
                return new DirectiveToken(directive, tokenString, locctr, 0);
            }
            case END -> {
                return new DirectiveToken(directive, tokenString, locctr, 0);
            }
            case LTORG -> {
                List<Literal> newLiterals = literalTable.resolveLiteralAddress(locctr);
                List<Numeric> numerics = newLiterals.stream()
                        .map(lit -> lit.getNumeric()).toList();
                int size = numerics.stream()
                        .map(num -> num.getSize())
                        .reduce(0, (acc, x) -> acc + x);
                return new ValueDirectiveToken(directive, numerics, tokenString, locctr, size);
            }
            case BYTE, WORD -> {
                symbolTable.putLabel(label.get(), locctr);
                Numeric numeric;
                String operand = stringToken.getOperands().get(0);

                if (operand.matches("[CX]'.*'") || operand.matches("[0-9].*")) {
                    // 전형적인 BYTE, WORD 형식에 맞는 경우
                    numeric = new Numeric(operand);
                } else {
                    // 그렇지 않은 경우 (ex: BUFEND-BUFFER)
                    numeric = new Numeric(0);
                    // TODO : modification 설정해주기
                }

                return new ValueDirectiveToken(directive, numeric, tokenString, locctr, numeric.getSize());
            }
            case RESB, RESW -> {
                symbolTable.putLabel(label.get(), locctr);
                int size;
                try {
                    size = Integer.parseInt(stringToken.getOperands().get(0));
                } catch (NumberFormatException e) {
                    throw new RuntimeException("failed to parse int : " + stringToken.getOperands().get(0));
                }
                if (directive == Directive.RESW) size *= 3;
                return new DirectiveToken(directive, tokenString, locctr, size);
            }
            case EQU -> {
                if (stringToken.getOperands().isEmpty())
                    throw new RuntimeException("EQU with no operand.");
                ;
                String expression = stringToken.getOperands().get(0);
                symbolTable.putLabel(label.get(), locctr, expression);
                // pass 2에서는 사용되지 않음
                return new DirectiveToken(directive, tokenString, 0, 0);
            }
            case EXTREF -> {
                for (String refSymbol : stringToken.getOperands()) {
                    symbolTable.putRefer(refSymbol);
                }
                // pass 2에서는 쓰이지 않을 예정
                return new DirectiveToken(directive, tokenString, 0, 0);
            }
            case EXTDEF -> {
                return new DirectiveToken(directive, stringToken.getOperands(), tokenString, 0, 0);
            }
        }
        throw new RuntimeException("Unknown directive : \n" + tokenString);
    }

    /**
     * pass2 작업을 수행한다. pass1에서 초기화한 토큰 테이블, 심볼 테이블 및 리터럴 테이블을 통해 오브젝트 코드를 생성한다.
     *
     * @return 해당 control section에 해당하는 오브젝트 코드 객체
     * @throws RuntimeException 소스 코드 컴파일 오류
     */
    public ObjectCode buildObjectCode() throws RuntimeException {
        ObjectCode objCode = new ObjectCode();

        for (Token token : tokens) {
            if (token instanceof ValueDirectiveToken) {
                List<Numeric> numerics = ((ValueDirectiveToken) token).getNumerics();
                String value = numerics.stream()
                        .map(numeric -> numeric.packValue())
                        .collect(Collectors.joining());
                objCode.addText(token.getAddress(), value);
            } else if (token instanceof InstructionToken) {
                handlePass2Instruction(objCode, (InstructionToken) token);
            } else if (token instanceof DirectiveToken) {
                handlePass2Directive(objCode, (DirectiveToken) token);
            } else
                throw new RuntimeException("invalid operation");
        }
        return objCode;
    }

    /**
     * @param objCode
     * @param token
     */
    private void handlePass2Instruction(ObjectCode objCode, InstructionToken token) {
        InstructionToken.TextInfo textInfo = token.getTextInfo(symbolTable);
        objCode.addText(token.getAddress(), textInfo.generateText());

        // todo modification
    }

    private void handlePass2Directive(ObjectCode objCode, DirectiveToken token) throws RuntimeException {
        Optional<String> csectName = symbolTable.getCsectName();
        if (csectName.isEmpty())
            throw new RuntimeException("invalid operation : there is no control section name.");

        Directive directive = token.getDirectiveType();

        switch (directive) {
            case START -> {
                objCode.setSectionName(csectName.get());
                objCode.setStartAddress(token.getAddress());
            }
            case CSECT -> {
                objCode.setSectionName(csectName.get());
                objCode.setStartAddress(0);
            }
            case EXTDEF -> {
                // TOOD
            }
            case END -> {
                // TODO
            }
            case RESB, RESW -> {
                // 새로운 텍스트 레코드 생성
                objCode.addTextNewLine(token.getAddress());
            }
            case EQU, BYTE, WORD, LTORG -> {
                // 처리할 동작 없음
            }
        }
    }

    /**
     * 심볼 테이블을 String으로 변환하여 반환한다. Assembler.java에서 심볼 테이블을 출력하는 데에 사용된다.
     *
     * @return 문자열로 변경된 심볼 테이블
     */
    public String getSymbolString() {
        return symbolTable.toString();
    }

    /**
     * 리터럴 테이블을 String으로 변환하여 반환한다. Assembler.java에서 리터럴 테이블을 출력하는 데에 사용된다.
     *
     * @return 문자열로 변경된 리터럴 테이블
     */
    public String getLiteralString() {
        return literalTable.toString();
    }

    private InstructionTable instTable;
    private ArrayList<Token> tokens;
    private SymbolTable symbolTable;
    private LiteralTable literalTable;
    private int locctr = 0;
}