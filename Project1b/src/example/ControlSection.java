package example; /**
 * @author Enoch Jung (github.com/enochjung)
 * @file ControlSection.java
 * @date 2024-05-05
 * @version 1.0.0
 * @brief 조교가 구현한 SIC/XE 어셈블러 코드 구조 샘플
 */

import example.directive.Directive;
import example.instruction.Instruction;
import example.instruction.InstructionTable;
import example.literal.LiteralTable;
import example.numeric.Numeric;
import example.symbol.Symbol;
import example.symbol.SymbolTable;
import example.token.DirectiveToken;
import example.token.InstructionToken;
import example.token.StringToken;
import example.token.Token;
import example.token.operand.NumericOperand;
import example.token.operand.Operand;

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
     * @throws RuntimeException 소스 코드 컴파일 오류.
     */
    public ControlSection(InstructionTable instTable, ArrayList<String> input) throws RuntimeException {
        List<StringToken> stringTokens = input.stream()
                .map(x -> new StringToken(x))
                .collect(Collectors.toList());
        SymbolTable symTab = new SymbolTable();
        LiteralTable litTab = new LiteralTable();

        ArrayList<Token> tokens = new ArrayList<Token>();

        int locctr = 0;
        for (StringToken stringToken : stringTokens) {
            if (stringToken.getOperator().isEmpty()) {
                boolean isLabelEmpty = stringToken.getLabel().isEmpty();
                boolean isOperandEmpty = stringToken.getOperands().isEmpty();
                if (!isLabelEmpty || !isOperandEmpty)
                    throw new RuntimeException("missing operator\n\n" + stringToken.toString());
                continue;
            }
            String operator = stringToken.getOperator().get();

            Optional<Instruction> optInst = instTable.search(operator);
            boolean isOperatorInstruction = optInst.isPresent();
            if (isOperatorInstruction) {
                Token token = handlePass1InstructionStep(optInst.get(), stringToken, locctr, symTab, litTab);
                locctr += token.getSize();
                tokens.add(token);
                System.out.println(token.toString()); /** 디버깅 용도 */
            } else {
                Token token = handlePass1DirectiveStep(stringToken, locctr, symTab, litTab);
                locctr += token.getSize();
                tokens.add(token);
                System.out.println(token.toString()); /** 디버깅 용도 */
            }
        }

        _tokens = tokens;
        _symbolTable = symTab;
        _literalTable = litTab;
    }

    /**
     * pass2 작업을 수행한다. pass1에서 초기화한 토큰 테이블, 심볼 테이블 및 리터럴 테이블을 통해 오브젝트 코드를 생성한다.
     *
     * @return 해당 control section에 해당하는 오브젝트 코드 객체
     * @throws RuntimeException 소스 코드 컴파일 오류.
     */
    public ObjectCode buildObjectCode() throws RuntimeException {
        ObjectCode objCode = new ObjectCode();
        Optional<Symbol> optRepSymbol = _symbolTable.getRepSymbol();
        if (optRepSymbol.isEmpty())
            throw new RuntimeException("invalid operation");
        Symbol repSymbol = optRepSymbol.get();

        for (Token token : _tokens) {
            if (token instanceof InstructionToken) {
                handlePass2InstructionStep(objCode, (InstructionToken) token);
            } else if (token instanceof DirectiveToken) {
                handlePass2DirectiveStep(objCode, (DirectiveToken) token, repSymbol, _literalTable);
            } else
                throw new RuntimeException("invalid operation");
        }

        return objCode;
    }

    /**
     * 심볼 테이블 객체의 정보를 문자열로 반환한다. Assembler.java에서 심볼 테이블 출력 용도로 사용한다.
     *
     * @return 심볼 테이블의 정보를 담은 문자열
     */
    public String getSymbolString() {
        return _symbolTable.toString();
    }

    /**
     * 리터럴 테이블 객체의 정보를 문자열로 반환한다. Assembler.java에서 리터럴 테이블 출력 용도로 사용한다.
     *
     * @return 리터럴 테이블의 정보를 담은 문자열
     */
    public String getLiteralString() {
        return _literalTable.toString();
    }

    /**
     * pass1에서 operator가 instruction에 해당하는 경우에 대해서 처리한다. label 및 operand에 출현한 심볼 및
     * 리터럴을 심볼 테이블 및 리터럴 테이블에 추가하고, 문자열 형태로 파싱된 토큰을 InstructionToken으로 가공하여 반환한다.
     *
     * @param inst   기계어 정보
     * @param token  문자열로 파싱된 토큰
     * @param locctr location counter 값
     * @param symTab 심볼 테이블
     * @param litTab 리터럴 테이블
     * @return 가공된 InstructionToken 객체
     * @throws RuntimeException 잘못된 명령어 사용 방식.
     */
    private static InstructionToken handlePass1InstructionStep(Instruction inst, StringToken token, int locctr,
                                                               SymbolTable symTab, LiteralTable litTab) throws RuntimeException {
        Instruction.Format format = inst.getFormat();
        Instruction.OperandType operandType = inst.getOperandType();

        int size;
        ArrayList<Operand> operands;
        boolean isN;
        boolean isI;
        boolean isX;
        boolean isP;
        boolean isE;

        switch (format) {
            case TWO:
                // TODO: size = 2?;
                break;

            case THREE_OR_FOUR:
                // TODO: size = 3 or 4?;
                break;

            default:
                throw new UnsupportedOperationException("not fully support instruction.InstructionInfo.Format");
        }

        // TODO: label을 심볼 테이블에 추가하기.

        switch (operandType) {
            case NO_OPERAND:
                // TODO: operand가 없어야 하는 경우에 대해서 처리하기.
                break;

            case MEMORY:
                // TODO: operand로 MEMORY 하나만 주어져야 하는 경우에 대해서 처리하기.

                String opd0 = "TODO";
                Operand.MemoryType memoryType = Operand.MemoryType.distinguish(opd0);

                switch (memoryType) {
                    case NUMERIC:
                        // TODO: operand로 상수 혹은 심볼이 주어지는 경우에 대해서 처리하기.

                        break;

                    case LITERAL:
                        // TODO: operand로 리터럴이 주어지는 경우에 대해서 처리하기.

                        break;

                    default:
                        throw new UnsupportedOperationException("not fully support Operand.MemoryType");
                }
                break;

            case REG:
                // TODO: operand로 REGISTER 하나만 주어져야 하는 경우에 대해서 처리하기.

                break;

            case REG1_REG2:
                // TODO: operand로 REGISTER 두개가 주어져야 하는 경우에 대해서 처리하기.

                break;

            default:
                throw new UnsupportedOperationException("not fully support instruction.InstructionInfo.OperandType");
        }
        return null;
//		return new InstructionToken(token.toString(), locctr, size, inst, operands, isN, isI, isX, isP, isE);
    }

    /**
     * pass1에서 operator가 directive에 해당하는 경우에 대해서 처리한다. label 및 operand에 출현한 심볼을 심볼
     * 테이블에 추가하고, 주소가 지정되지 않은 리터럴을 리터럴 테이블에서 찾아 주소를 할당하고, 문자열 형태로 파싱된 토큰을
     * DirectiveToken으로 가공하여 반환한다.
     *
     * @param token  문자열로 파싱된 토큰
     * @param locctr location counter 값
     * @param symTab 심볼 테이블
     * @param litTab 리터럴 테이블
     * @return 가공된 DirectiveToken 객체
     * @throws RuntimeException 잘못된 지시어 사용 방식.
     */
    private static DirectiveToken handlePass1DirectiveStep(StringToken token, int locctr, SymbolTable symTab,
                                                           LiteralTable litTab) throws RuntimeException {
        String operator = token.getOperator().get();

        Directive directive;
        try {
            directive = Directive.stringToDirective(operator);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage() + "\n\n" + token.toString());
        }

        int size;
        ArrayList<Operand> operands;

        switch (directive) {
            case START:
                // TODO: START인 경우에 대해서 pass1 처리하기.

                break;

            case CSECT:
                // TODO: CSECT인 경우에 대해서 pass1 처리하기.

                break;

            case EXTDEF:
                // TODO: EXTDEF인 경우에 대해서 pass1 처리하기.

                break;

            case EXTREF:
                // TODO: EXTREF인 경우에 대해서 pass1 처리하기.

                break;

            case BYTE:
                // TODO: BYTE인 경우에 대해서 pass1 처리하기.

                break;

            case WORD:
                // TODO: WORD인 경우에 대해서 pass1 처리하기.

                break;

            case RESB:
                // TODO: RESB인 경우에 대해서 pass1 처리하기.

                break;

            case RESW:
                // TODO: RESW인 경우에 대해서 pass1 처리하기.

                break;

            case LTORG:
                // TODO: LTORG인 경우에 대해서 pass1 처리하기.

                break;

            case EQU:
                // TODO: EQU인 경우에 대해서 pass1 처리하기.

                break;

            case END:
                // TODO: END인 경우에 대해서 pass1 처리하기.

                break;

            default:
                throw new UnsupportedOperationException("not fully support directive.Directive");
        }

//		return new DirectiveToken(token.toString(), locctr, size, directive, operands);
        return null;
    }

    /**
     * pass2에서 operator가 instruction인 경우에 대해서 오브젝트 코드에 정보를 추가한다.
     *
     * @param objCode 오브젝트 코드 객체
     * @param token   InstructionToken 객체
     * @throws RuntimeException 잘못된 심볼 객체 변환 시도.
     */
    private static void handlePass2InstructionStep(ObjectCode objCode, InstructionToken token) throws RuntimeException {
        Token.TextInfo textInfo = token.getTextInfo();

        objCode.addText(textInfo.address, textInfo.code, textInfo.size);

        if (textInfo.mod.isEmpty())
            return;
        Token.ModificationInfo modInfo = textInfo.mod.get();

        for (String refer : modInfo.refers)
            objCode.addModification(refer, modInfo.address, modInfo.sizeHalfByte);
    }

    /**
     * pass2에서 operator가 directive인 경우에 대해서 오브젝트 코드에 정보를 추가한다.
     *
     * @param objCode      오브젝트 코드 객체
     * @param token        DirectiveToken 객체
     * @param repSymbol    control section 명칭 심볼
     * @param literalTable 리터럴 테이블
     * @throws RuntimeException 잘못된 지시어 사용 방식.
     */
    private static void handlePass2DirectiveStep(ObjectCode objCode, DirectiveToken token, Symbol repSymbol,
                                                 LiteralTable literalTable) throws RuntimeException {
        Directive directive = token.getDirective();
        String sectionName = repSymbol.getName();

        ArrayList<Operand> operands;
        NumericOperand numOperand;
        Numeric num;
        int address;
        int size;

        switch (directive) {
            case START:
                numOperand = (NumericOperand) token.getOperands().get(0);
                objCode.setSectionName(sectionName);
                objCode.setStartAddress(numOperand.getNumeric().getInteger());
                break;

            case CSECT:
                objCode.setSectionName(sectionName);
                objCode.setStartAddress(0);
                break;

            case EXTDEF:
                // TODO: EXTDEF인 경우에 대해서 pass2 처리하기.
                // objCode.addDefineSymbol(?, ?);

                break;

            case EXTREF:
                operands = token.getOperands();
                for (Operand operand : operands) {
                    numOperand = (NumericOperand) operand;
                    num = numOperand.getNumeric();
                    String symbolName = num.getName().get();
                    objCode.addReferSymbol(symbolName);
                }
                break;

            case BYTE:
                // TODO: BYTE인 경우에 대해서 pass2 처리하기.
                // objCode.addText(?, ?, ?);

                break;

            case WORD:
                // TODO: WORD인 경우에 대해서 pass2 처리하기.
                // objCode.addText(?, ?, ?);
                // ArrayList<String> refers = ?;
                // for (String refer : refers) objCode.addModification(refer, ?, ?);

                break;

            case LTORG:
                // TODO: LTORG인 경우에 대해서 pass2 처리하기.
                // objCode.addText(?, ?, ?);

                break;

            case END:
                // TODO: END인 경우에 대해서 pass2 처리하기.
                // objCode.addText(?, ?, ?);
                // objCode.setInitialPC(?);
                // objCode.setProgramLength(?);

                break;

            case RESB:
            case RESW:
            case EQU:
                // 처리할 동작이 없음.
                break;

            default:
                throw new UnsupportedOperationException("not fully support directive.Directive");
        }
    }

    private final List<Token> _tokens;
    private final SymbolTable _symbolTable;
    private final LiteralTable _literalTable;
}
