import directive.Directive;
import instruction.Instruction;
import instruction.InstructionTable;
import literal.LiteralTable;
import symbol.Symbol;
import symbol.SymbolTable;
import token.DirectiveToken;
import token.InstructionToken;
import token.StringToken;
import token.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

        int locctr = 0;
        for (StringToken stringToken : stringTokens) {
            if (stringToken.getOperator().isEmpty()) {
                continue;
            }

            String operator = stringToken.getOperator().get();
            Optional<Instruction> inst = instTable.search(operator);

            Token token;
            if (inst.isPresent()) {
                // 명령어 수행
                token = handlePass1Instruction(locctr, stringToken, inst.get(), symbolTable, literalTable);
            } else {
                // 지시어 수행
                token = handlePass1Directive(locctr, stringToken, symbolTable, literalTable);
            }
            locctr += token.getSize();
            tokens.add(token);
        }
    }

    /**
     * pass2 작업을 수행한다. pass1에서 초기화한 토큰 테이블, 심볼 테이블 및 리터럴 테이블을 통해 오브젝트 코드를 생성한다.
     *
     * @return 해당 control section에 해당하는 오브젝트 코드 객체
     * @throws RuntimeException 소스 코드 컴파일 오류
     */
    public ObjectCode buildObjectCode() throws RuntimeException {
        ObjectCode objCode = new ObjectCode();

        // TODO: pass2 수행하기.

        return objCode;
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

    private static InstructionToken handlePass1Instruction(
            int locctr,
            StringToken stringToken,
            Instruction instruction,
            SymbolTable symbolTable,
            LiteralTable literalTable) throws RuntimeException {
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

    private static DirectiveToken handlePass1Directive(
            int locctr,
            StringToken stringToken,
            SymbolTable symbolTable,
            LiteralTable literalTable
    ) throws RuntimeException {
        assert stringToken.getOperator().isPresent();

        Directive directive = Directive.fromString(stringToken.getOperator().get());
        switch (directive) {
            case START, CSECT -> {
                symbolTable.setCsectName("");
            }
            case LTORG -> {
            }
            case BYTE -> {
            }
            case WORD -> {
            }
            case RESB -> {
            }
            case RESW -> {
            }
            case EQU -> {
            }
            case END -> {
            }
        }
        return new DirectiveToken(directive, stringToken.getTokenString(), 0, 0);
    }

    /**
     * 기계어 목록 테이블
     */
    private InstructionTable instTable;

    /**
     * 토큰 테이블
     */
    private ArrayList<Token> tokens;

    /**
     * 심볼 테이블
     */
    private SymbolTable symbolTable;

    /**
     * 리터럴 테이블
     */
    private LiteralTable literalTable;
}