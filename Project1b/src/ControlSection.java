import java.util.ArrayList;
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
		_instTable = instTable;
		_tokens = new ArrayList<Token>();
		_symbolTable = new SymbolTable();
		_literalTable = new LiteralTable();

		// TODO: pass1 수행하기.
		int locctr = 0;
		for (String line : input) {
			Token tok = new Token(line);
			_tokens.add(tok);

			Optional<InstructionInfo> instInfo = instTable.search(tok.getOperator());
			if (instInfo.isEmpty()) {
				switch (tok.getOperator()) {
					case "START":
						locctr = Integer.parseInt(tok.getOperands().get(0));
						break;
					case "CSECT":
						locctr = 0;
						break;
					case "EQU":
						break;
					case "LTORG":
						break;
					default:
						locctr += handleResDirectives(tok.getOperator(), tok.getOperands().get(0));
						break;
				}
			} else {
				InstructionInfo inst = instInfo.get();
				locctr += inst.getFormat() + (tok.isE() ? 1 : 0);
			}


		}
	}

	int handleResDirectives(String directive, String data) {
		switch (directive) {
			case "RESW":
				return Integer.parseInt(data) * 3;
			case "RESB":
				return Integer.parseInt(data);
			case "WORD":
				return 3;
			case "BYTE":
				if (data.startsWith("C")) {
					return data.length() - 3; // C, 따옴표 두 개 제외
				} else if (data.startsWith("X")) {
					return (data.length() - 3 + 1) / 2; // X, 따옴표 2개 제외 (두 문자 당 1byte)
				}
				break;
		}
		return 0;
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
		return _symbolTable.toString();
	}

	/**
	 * 리터럴 테이블을 String으로 변환하여 반환한다. Assembler.java에서 리터럴 테이블을 출력하는 데에 사용된다.
	 * 
	 * @return 문자열로 변경된 리터럴 테이블
	 */
	public String getLiteralString() {
		return _literalTable.toString();
	}

	/** 기계어 목록 테이블 */
	private InstructionTable _instTable;

	/** 토큰 테이블 */
	private ArrayList<Token> _tokens;

	/** 심볼 테이블 */
	private SymbolTable _symbolTable;

	/** 리터럴 테이블 */
	private LiteralTable _literalTable;
}