package instruction;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InstructionTable {
    /**
     * 기계어 목록 파일을 읽어, 기계어 목록 테이블을 초기화한다.
     *
     * @param instFileName 기계어 목록이 적힌 파일
     * @throws FileNotFoundException 기계어 목록 파일 미존재
     * @throws IOException           파일 읽기 실패
     */
    public InstructionTable(String instFileName) throws IOException {
        File file = new File(instFileName);
        BufferedReader bufReader = new BufferedReader(new FileReader(file));

        String line;
        while ((line = bufReader.readLine()) != null) {
            Instruction inst = new Instruction(line);
            instructionMap.put(inst.getOpcode(), inst);
        }
        bufReader.close();
    }

    /**
     * 기계어 목록 테이블에서 특정 기계어를 검색한다.
     *
     * @param opcode 검색할 기계어 명칭
     * @return 기계어 정보. 없을 경우 empty
     */
    public Optional<Instruction> search(int opcode) {
        if (instructionMap.containsKey(opcode)) {
            return Optional.ofNullable(instructionMap.get(opcode));
        }
        return Optional.empty();
    }

    /**
     * 기계어 목록 테이블. key: opcode, value: 기계어 정보
     */
    private final Map<Integer, Instruction> instructionMap = new HashMap<>();

    // test
    public static void main(String[] args) throws Exception {
        InstructionTable table = new InstructionTable("inst_table.txt");
        System.out.println(table.search(0x00).get().getName()); // LDA
    }
}