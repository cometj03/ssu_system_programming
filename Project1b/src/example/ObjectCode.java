package example; /**
 * @author Enoch Jung (github.com/enochjung)
 * @file ObjectCode.java
 * @date 2024-05-05
 * @version 1.0.0
 *
 * @brief 조교가 구현한 SIC/XE 어셈블러 코드 구조 샘플
 */

import java.util.Optional;
import java.util.ArrayList;

import example.numeric.Numeric;

public class ObjectCode {
	public ObjectCode() {
		_sectionName = Optional.empty();
		_startAddress = Optional.empty();
		_programLength = Optional.empty();
		_initialPC = Optional.empty();

		_defines = new ArrayList<Define>();
		_refers = new ArrayList<String>();
		_texts = new ArrayList<Text>();
		_mods = new ArrayList<Modification>();
	}

	/**
	 * ObjectCode 객체를 String으로 변환한다. Assembler.java에서 오브젝트 코드를 출력하는 데에 사용된다.
	 */
	@Override
	public String toString() {
		if (_sectionName.isEmpty() || _startAddress.isEmpty() || _programLength.isEmpty())
			throw new RuntimeException("illegal operation");

		String sectionName = _sectionName.get();
		int startAddress = _startAddress.get();
		int programLength = _programLength.get();

		String header = String.format("H%-6s%06X%06X\n", sectionName, startAddress, programLength);

		// TODO: 오브젝트 코드 문자열 생성하기.
		String define = "TODO";
		String refer = "TODO";
		String text = "TODO";
		String modification = "TODO";

		String end = "E" + _initialPC
				.map(x -> String.format("%06X", x))
				.orElse("");

		return header + define + refer + text + modification + end;
	}

	public void setSectionName(String sectionName) {
		_sectionName = Optional.of(sectionName);
	}

	public void setStartAddress(int address) {
		_startAddress = Optional.of(address);
	}

	public void setProgramLength(int length) {
		_programLength = Optional.of(length);
	}

	public void addDefineSymbol(String symbolName, int address) {
		_defines.add(new Define(symbolName, address));
	}

	public void addReferSymbol(String symbolName) {
		_refers.add(symbolName);
	}

	public void addText(int address, Numeric context, int size) {
		String value = context.getValue(size * 2);
		_texts.add(new Text(address, value));
	}

	public void addModification(String symbolNameWithSign, int address, int sizeHalfByte) {
		_mods.add(new Modification(address, sizeHalfByte, symbolNameWithSign));
	}

	public void setInitialPC(int address) {
		_initialPC = Optional.of(address);
	}

	class Define {
		Define(String symbolName, int address) {
			this.symbolName = symbolName;
			this.address = address;
		}

		String symbolName;
		int address;
	}

	class Text {
		Text(int address, String value) {
			this.address = address;
			this.value = value;
		}

		int address;
		String value;
	}

	class Modification {
		Modification(int address, int sizeHalfByte, String symbolNameWithSign) {
			this.address = address;
			this.sizeHalfByte = sizeHalfByte;
			this.symbolNameWithSign = symbolNameWithSign;
		}

		int address;
		int sizeHalfByte;
		String symbolNameWithSign;
	}

	private Optional<String> _sectionName;
	private Optional<Integer> _startAddress;
	private Optional<Integer> _programLength;
	private Optional<Integer> _initialPC;

	private ArrayList<Define> _defines;
	private ArrayList<String> _refers;
	private ArrayList<Text> _texts;
	private ArrayList<Modification> _mods;
}
