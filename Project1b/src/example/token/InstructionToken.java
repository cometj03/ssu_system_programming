/**
 * @author Enoch Jung (github.com/enochjung)
 * @file InstructionToken.java
 * @date 2024-05-05
 * @version 1.0.0
 *
 * @brief 조교가 구현한 SIC/XE 어셈블러 코드 구조 샘플
 */

package example.token;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Optional;

import example.instruction.Instruction;
import example.numeric.Numeric;
import example.token.operand.*;

public class InstructionToken extends Token {
	public InstructionToken(String tokenString, int address, int size, Instruction inst, ArrayList<Operand> operands,
			boolean nBit, boolean iBit, boolean xBit, boolean pBit, boolean eBit) {
		super(tokenString, address, size);
		_inst = inst;
		_operands = operands;
		_nBit = nBit;
		_iBit = iBit;
		_xBit = xBit;
		_pBit = pBit;
		_eBit = eBit;
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
	 * 토큰의 Base relative bit가 1인지 여부를 반환한다.
	 * 
	 * @return B bit가 1인지 여부
	 */
	public boolean isB() {
		return false;
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
	 * InstructionToken 객체의 정보를 문자열로 반환한다. 디버그 용도로 사용한다.
	 */
	@Override
	public String toString() {
		String instName = _inst.getName();
		String operands = _operands.isEmpty() ? "(empty)"
				: (_operands.stream()
						.map(x -> x.toString())
						.collect(Collectors.joining("/")));
		String nixbpe = String.format("0b%d%d%d%d%d%d", _nBit ? 1 : 0, _iBit ? 1 : 0, _xBit ? 1 : 0, 0, _pBit ? 1 : 0,
				_eBit ? 1 : 0);
		return "InstructionToken{name:" + instName + ", operands:" + operands + ", nixbpe:" + nixbpe + "}";
	}

	/**
	 * object code에 관한 정보를 반환한다.
	 * 
	 * @return 텍스트 레코드 정보가 담긴 객체
	 * @throws RuntimeException 잘못된 심볼 객체 변환 시도.
	 */
	public TextInfo getTextInfo() throws RuntimeException {
		int address = 0;
		Numeric code = null;
		int size = 0;
		Optional<ModificationInfo> modInfo = null;

		// TODO: pass2 과정 중, 오브젝트 코드 생성을 위한 정보를 TextInfo 객체에 담아서 반환하기.

		TextInfo textInfo = new TextInfo(address, code, size, modInfo);

		return textInfo;
	}

	private Instruction _inst;

	private ArrayList<Operand> _operands;

	private boolean _nBit;
	private boolean _iBit;
	private boolean _xBit;
	// private boolean _bBit; /** base relative는 구현하지 않음 */
	private boolean _pBit;
	private boolean _eBit;
}
