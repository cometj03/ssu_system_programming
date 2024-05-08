/**
 * @author Enoch Jung (github.com/enochjung)
 * @file StringToken.java
 * @date 2024-05-05
 * @version 1.0.0
 *
 * @brief 조교가 구현한 SIC/XE 어셈블러 코드 구조 샘플
 */

package example.token;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class StringToken {
	/**
	 * 소스 코드 한 줄에 해당하는 토큰을 초기화한다.
	 * 
	 * @param input 소스 코드 한 줄에 해당하는 문자열
	 * @throws RuntimeException 잘못된 형식의 소스 코드 파싱 시도.
	 */
	public StringToken(String input) throws RuntimeException {
		// TODO: 소스 코드를 파싱하여 토큰을 초기화하기.
		// _label = ?;
		// _operator = ?;
		// _operands = ?;
		// _comment = ?;
		// _nBit = ?;
		// _iBit = ?;
		// _xBit = ?;
		// _pBit = ?;
		// _eBit = ?;

		System.out.println(this.toString()); /** 디버깅 용도 */
	}

	/**
	 * label 문자열을 반환한다.
	 * 
	 * @return label 문자열. 없으면 empty <code>Optional</code>.
	 */
	public Optional<String> getLabel() {
		return _label;
	}

	/**
	 * operator 문자열을 반환한다.
	 * 
	 * @return operator 문자열. 없으면 empty <code>Optional</code>.
	 */
	public Optional<String> getOperator() {
		return _operator;
	}

	/**
	 * operand 문자열 배열을 반환한다.
	 * 
	 * @return operand 문자열 배열
	 */
	public ArrayList<String> getOperands() {
		return _operands;
	}

	/**
	 * comment 문자열을 반환한다.
	 * 
	 * @return comment 문자열. 없으면 empty <code>Optional</code>.
	 */
	public Optional<String> getComment() {
		return _comment;
	}

	/**
	 * 토큰의 iNdirect bit가 1인지 여부를 반환한다.
	 * 
	 * @return N bit가 1인지 여부
	 */
	public boolean isN() {
		return _nBit;
	}

	/**
	 * 토큰의 Immediate bit가 1인지 여부를 반환한다.
	 * 
	 * @return I bit가 1인지 여부
	 */
	public boolean isI() {
		return _iBit;
	}

	/**
	 * 토큰의 indeX bit가 1인지 여부를 반환한다.
	 * 
	 * @return X bit가 1인지 여부
	 */
	public boolean isX() {
		return _xBit;
	}

	/**
	 * 토큰의 Pc relative bit가 1인지 여부를 반환한다.
	 * 
	 * @return P bit가 1인지 여부
	 */
	public boolean isP() {
		return _pBit;
	}

	/**
	 * 토큰의 Extra bit가 1인지 여부를 반환한다.
	 * 
	 * @return E bit가 1인지 여부
	 */
	public boolean isE() {
		return _eBit;
	}

	/**
	 * StringToken 객체의 정보를 문자열로 반환한다. 디버그 용도로 사용한다.
	 */
	@Override
	public String toString() {
		String label = _label.map(x -> "<" + x + ">").orElse("(no label)");
		String operator = (isE() ? "+" : "") + _operator.map(x -> "<" + x + ">").orElse("(no operator)");
		String operand = (isN() && !isI() ? "@" : "") + (isI() && !isN() ? "#" : "")
				+ (_operands.isEmpty() ? "(no operand)"
						: "<" + _operands.stream().collect(Collectors.joining("/")) + ">")
				+ (isX() ? (_operands.isEmpty() ? "X" : "/X") : "");
		String comment = _comment.map(x -> "<" + x + ">").orElse("(no comment)");

		String formatted = String.format("%-12s\t%-12s\t%-18s\t%s", label, operator, operand, comment);
		return formatted;
	}

	private Optional<String> _label;
	private Optional<String> _operator;
	private ArrayList<String> _operands;
	private Optional<String> _comment;

	private boolean _nBit;
	private boolean _iBit;
	private boolean _xBit;
	// private boolean _bBit; /** base relative는 구현하지 않음 */
	private boolean _pBit;
	private boolean _eBit;
}
