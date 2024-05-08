/**
 * @author Enoch Jung (github.com/enochjung)
 * @file LiteralTable.java
 * @date 2024-05-05
 * @version 1.0.0
 *
 * @brief 조교가 구현한 SIC/XE 어셈블러 코드 구조 샘플
 */

package example.literal;

import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.stream.Collectors;

public class LiteralTable {
	/**
	 * 리터럴 테이블을 초기화한다.
	 */
	public LiteralTable() {
		_literalMap = new LinkedHashMap<String, Literal>();
	}

	/**
	 * 리터럴을 리터럴 테이블에 추가한다.
	 * 
	 * @param literal 추가할 리터럴
	 * @throws RuntimeException 비정상적인 리터럴 서식 혹은 이미 존재하는 리터럴 추가를 시도
	 */
	public Literal putLiteral(String literal) throws RuntimeException {
		// TODO: 리터럴 객체를 생성하고, 이를 리터럴 테이블에 추가하기.
		Literal lit;

		return lit;
	}

	/**
	 * 리터럴 문자열을 통해 리터럴을 찾는다.
	 * 
	 * @param literal 찾을 리터럴 문자열
	 * @return 리터럴. 없을 경우 empty <code>Optional</code>
	 */
	public Optional<Literal> search(String literal) {
		// TODO: 리터럴을 검색하고, 결과를 반환하기
		return something;
	}

	/**
	 * 리터럴 주소값을 통해 리터럴을 찾는다.
	 * 
	 * @param address 찾을 리터럴의 시작 주소
	 * @return 리터럴. 없을 경우 empty <code>Optional</code>
	 */
	public Optional<Literal> search(int address) {
		// TODO: 리터럴 주소값으로 리터럴을 검색하고, 결과를 반환하기.
		return something;
	}

	/**
	 * 리터럴 테이블에서 주소가 할당되지 않은 리터럴에 대해 주소를 할당하고, 해당 리터럴들의 전체 크기를 반환한다.
	 * 
	 * @param address 할당을 시작할 주소
	 * @return 할당된 리터럴들의 총 크기
	 */
	public int assignAddress(int address) {
		// TODO: 리터럴 주소값 할당하기.
		int size;

		return size;
	}

	/**
	 * 리터럴 테이블 객체의 정보를 문자열로 반환한다. 리터럴 테이블 출력 용도로 사용한다.
	 */
	@Override
	public String toString() {
		String literals = _literalMap.entrySet().stream()
				.map(x -> x.getValue().toString())
				.collect(Collectors.joining("\n"));

		return literals;
	}

	private LinkedHashMap<String, Literal> _literalMap;
}
