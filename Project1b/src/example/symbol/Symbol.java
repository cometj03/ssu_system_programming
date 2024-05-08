/**
 * @author Enoch Jung (github.com/enochjung)
 * @file Symbol.java
 * @date 2024-05-05
 * @version 1.0.0
 *
 * @brief 조교가 구현한 SIC/XE 어셈블러 코드 구조 샘플
 */

package example.symbol;

import java.util.Optional;

import example.numeric.Numeric;

public class Symbol {
	/**
	 * 문자열이 심볼 문자열 형태인지 판별한다.
	 * 
	 * @param symbol 판별할 문자열
	 * @return 심볼 문자열로 사용 가능한 형태인지 여부
	 */
	public static boolean isSymbol(String symbol) {
		String symbolRegex = "^[a-zA-Z][a-zA-Z0-9]*$";
		return symbol.matches(symbolRegex) && symbol.length() <= 6;
	}

	/**
	 * 심볼 명칭을 반환한다.
	 * 
	 * @return 심볼 명칭
	 */
	public String getName() {
		return _name;
	}

	/**
	 * 상대 주소로 표현될 수 없는 심볼인지 (START,CSECT,EXTREF로 생성된 심볼인지) 판별한다.
	 * 
	 * @return 상대 주소로 표현될 수 없는지 여부
	 */
	public boolean isBaseSymbol() {
		return _state == State.REP_SECTION || _state == State.EXTERNAL;
	}

	/**
	 * EXTREF로 생성된 심볼인지 판별한다.
	 * 
	 * @return EXTREF로 생성된 심볼인지 여부
	 */
	public boolean isReferSymbol() {
		return _state == State.EXTERNAL;
	}

	/**
	 * 심볼의 정보를 문자열로 반환한다. 디버그 용도로 사용한다.
	 */
	@Override
	public String toString() {
		String name = _name;
		String address = _address
				.map(x -> x.toString())
				.map(x -> x.replace("+" + name, ""))
				.map(x -> x.replace("+", " \t+ "))
				.orElse("(not assigned)");
		String formatted = String.format("%-12s%s", name, _state == State.EXTERNAL ? "REF" : address);
		return formatted;
	}

	public Optional<Numeric> getAddress() {
		return _address;
	}

	/**
	 * 대표 심볼 객체를 초기화한다.
	 * 
	 * @param name    심볼 명칭
	 * @param address 절대 주소
	 * @return 대표 심볼 객체
	 * @throws RuntimeException 부적절한 심볼 명칭
	 */
	static Symbol createRepSymbol(String name, int address) throws RuntimeException {
		Symbol symbol = new Symbol(name, Optional.empty(), State.REP_SECTION);
		// TODO: symbol 객체의 address 할당하기.

		return symbol;
	}

	/**
	 * 외부 심볼 객체를 초기화한다.
	 * 
	 * @param name 심볼 명칭
	 * @return 외부 심볼 객체
	 * @throws RuntimeException 부적절한 심볼 명칭
	 */
	static Symbol createExternalSymbol(String name) throws RuntimeException {
		// TODO: 외부 심볼 객체 생성하기.
		// Symbol symbol = new Symbol(?, ?, ?);
		// and something more...?

//		return symbol;
		return null;
	}

	/**
	 * 주소값이 주어진 일반 심볼 객체를 초기화한다.
	 * 
	 * @param name    심볼 명칭
	 * @param address 주소값
	 * @return 일반 심볼 객체
	 * @throws RuntimeException 부적절한 심볼 명칭
	 */
	static Symbol createAddressAssignedSymbol(String name, Numeric address) throws RuntimeException {
		Symbol symbol = new Symbol(name, Optional.of(address), State.ADDRESS_ASSIGNED);
		return symbol;
	}

	/**
	 * 주소값이 주어지지 않은 일반 심볼 객체를 초기화한다.
	 * 
	 * @param name 심볼 명칭
	 * @return 일반 심볼 객체
	 * @throws RuntimeException 부적절한 심볼 명칭
	 */
	static Symbol createAddressNotAssignedSymbol(String name) throws RuntimeException {
		Symbol symbol = new Symbol(name, Optional.empty(), State.ADDRESS_NOT_ASSIGNED);
		return symbol;
	}

	/**
	 * 주소값이 주어지지 않은 일반 심볼 객체의 주소를 설정한다.
	 * 
	 * @param address 주소값
	 * @throws RuntimeException 주소값이 주어지지 않은 일반 심볼이 아님
	 */
	void assign(Numeric address) throws RuntimeException {
		// TODO: 주소값 설정하기.
	}

	private Symbol(String name, Optional<Numeric> address, State state) throws RuntimeException {
		if (!isSymbol(name))
			throw new RuntimeException("illegal symbol name");
		_name = name;
		_address = address;
		_state = state;
	}

	/**
	 * 심볼의 상태값
	 * 
	 * <ul>
	 * <li><code>State.REP_SECTION</code>: control section의 명칭으로 선언한 대표 심볼
	 * <li><code>State.EXTERNAL</code>: EXTREF로 선언한 외부 심볼
	 * <li><code>State.ADDRESS_ASSIGNED</code>: label에서 등장하여 주소값이 결정된 일반 심볼
	 * <li><code>State.ADDRESS_NOT_ASSIGNED</code>: operand에서 등장하였으나, 아직 label에서는
	 * 등장하지 않아 주소값이 결정되지 않은 일반 심볼
	 * </ul>
	 */
	private enum State {
		/**
		 * control section의 명칭으로 선언한 대표 심볼
		 */
		REP_SECTION,

		/**
		 * EXTREF로 선언한 외부 심볼
		 */
		EXTERNAL,

		/**
		 * label에서 등장하여 주소값이 결정된 일반 심볼
		 */
		ADDRESS_ASSIGNED,

		/**
		 * operand에서 등장하였으나, 아직 label에서는 등장하지 않아 주소값이 결정되지 않은 일반 심볼
		 */
		ADDRESS_NOT_ASSIGNED;
	}

	private final String _name;
	private Optional<Numeric> _address;
	private State _state;
}
