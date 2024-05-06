import java.io.*;
import java.util.HashMap;
import java.util.Optional;

public class InstructionTable {
	/**
	 * 기계어 목록 파일을 읽어, 기계어 목록 테이블을 초기화한다.
	 * 
	 * @param instFileName 기계어 목록이 적힌 파일
	 * @throws FileNotFoundException 기계어 목록 파일 미존재
	 * @throws IOException           파일 읽기 실패
	 */
	public InstructionTable(String instFileName) throws FileNotFoundException, IOException {
		instructionMap = new HashMap<String, InstructionInfo>();

		File file = new File(instFileName);
		BufferedReader bufReader = new BufferedReader(new FileReader(file));

		String line;
		while ((line = bufReader.readLine()) != null) {
			InstructionInfo inst = new InstructionInfo(line);
			instructionMap.put(inst.getName(), inst);
		}
		bufReader.close();
	}

	/**
	 * 기계어 목록 테이블에서 특정 기계어를 검색한다.
	 * 
	 * @param instructionName 검색할 기계어 명칭
	 * @return 기계어 정보. 없을 경우 empty
	 */
	public Optional<InstructionInfo> search(String instructionName) {
		InstructionInfo inst = instructionMap.get(instructionName);
		if (inst != null) return Optional.of(inst);
		return Optional.empty();
	}

	/** 기계어 목록 테이블. key: 기계어 명칭, value: 기계어 정보 */
	private HashMap<String, InstructionInfo> instructionMap;
}