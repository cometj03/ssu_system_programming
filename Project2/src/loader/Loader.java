package loader;

import resource.Memory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Object code를 SIC/XE 가상 머신의 메모리에 로드하는 모듈
 * <br/>
 * SIC/XE object code를 파싱하고, 파싱한 정보를 토대로 ResourceManager 내에
 * 변수로 지정되어 있는 가상의 메모리 영역에 로드함
 */
public class Loader {

    public static class LoaderInfo {
        public String programName = null;
        public int programStartAddr = 0;
    }

    public LoaderInfo loadFromFile(Memory memory, String objFileName, int programLoadAddr) throws IOException {
        File file = new File(objFileName);
        BufferedReader bufReader = new BufferedReader(new FileReader(file));
        List<List<String>> programs = new ArrayList<>();

        String line;
        List<String> records = new ArrayList<>();
        while ((line = bufReader.readLine()) != null) {
            records.add(line);
            if (line.startsWith("E")) {
                programs.add(records);
                records = new ArrayList<>();
            }
        }
        bufReader.close();
        return loadAll(memory, programs, programLoadAddr);
    }

    private LoaderInfo loadAll(Memory memory, List<List<String>> programs, int programLoadAddr) {
        ExternalSymbolTable estab = new ExternalSymbolTable();
        LoaderInfo loaderInfo = new LoaderInfo();

        // pass 1
        // external symbol table 채우기
        int csectAddr = programLoadAddr;
        for (List<String> program : programs) {

            Symbol headerSymbol = new Symbol();

            for (String record : program) {
                if (record.startsWith("H")) {
                    String csectName = record.substring(1, 7).trim();
                    if (loaderInfo.programName == null)
                        loaderInfo.programName = csectName;

                    int relativeStartAddr = Integer.parseInt(record.substring(7, 13), 16);
                    csectAddr += relativeStartAddr;

                    int csectLength = Integer.parseInt(record.substring(13, 19), 16);

                    headerSymbol.setCsectName(csectName);
                    headerSymbol.setLength(csectLength);
                    headerSymbol.setAddress(csectAddr);
                    estab.addSymbol(headerSymbol);
                } else if (record.startsWith("D")) {
                    for (int i = 1; i <= record.length() - 12; i += 12) {
                        String symbolName = record.substring(i, i + 6).trim();
                        int symbolAddr = Integer.parseInt(record.substring(i + 6, i + 12).trim(), 16);

                        Symbol symbol = new Symbol();
                        symbol.setSymbolName(symbolName);
                        symbol.setAddress(symbolAddr + headerSymbol.address.orElse(0));
                        estab.addSymbol(symbol);
                    }
                } else if (record.startsWith("E")) {
                    if (record.length() >= 7)
                        loaderInfo.programStartAddr = Integer.parseInt(record.substring(1, 7), 16) + programLoadAddr;
                    if (headerSymbol.length.isPresent())
                        csectAddr += headerSymbol.length.get();
                }
            }
        }

        // pass 2
        // 실제 메모리에 로드한 후 modification 레코드 반영
        csectAddr = programLoadAddr;
        for (List<String> program : programs) {
            csectAddr += loadProgram(memory, program, csectAddr, estab);
        }

        return loaderInfo;
    }

    /**
     * @param memory           프로그램을 올릴 가상의 메모리
     * @param program          하나의 프로그램에 해당하는 오브젝트 프로그램 (header로 시작해서 end 레코드로 끝남)
     * @param programStartAddr 프로그램을 메모리에 로드할 시작 주소
     * @return 로드 된 프로그램의 총 길이
     */
    private int loadProgram(Memory memory, List<String> program, int programStartAddr, ExternalSymbolTable estab) {
        int length = -1;

        for (String record : program) {
            if (record.startsWith("H")) {
                length = Integer.parseInt(record.substring(13, 19), 16);
            } else if (record.startsWith("T")) {
                int startAddrRelative = Integer.parseInt(record.substring(1, 7), 16);
                int recordLen = Integer.parseInt(record.substring(7, 9), 16);
                String text = record.substring(9, 9 + recordLen * 2);
                memory.setMemString(startAddrRelative + programStartAddr, text);
            } else if (record.startsWith("M")) {
                int startLocationRelative = Integer.parseInt(record.substring(1, 7), 16);
                int startLocationAbsolute = startLocationRelative + programStartAddr;
                int halfByteLen = Integer.parseInt(record.substring(7, 9), 16);
                int memValue = memory.getMemValue(startLocationAbsolute, (halfByteLen + 1) / 2);
                Optional<Symbol> targetSymbol = estab.searchByName(record.substring(10));

                if (targetSymbol.isEmpty())
                    throw new RuntimeException("Using not defined symbol. target : " + record.substring(10));

                // 오버플로우 방지를 위한 마스크
                int mask = 0;
                for (int i = 0; i < halfByteLen; i++)
                    mask |= 0xF << (i * 4);

                if (record.charAt(9) == '+') {
                    int rest = memValue & (0xFFFFFFFF - mask);
                    memValue = rest + (((memValue & mask) + targetSymbol.get().address.get()) & mask);
                } else if (record.charAt(9) == '-') {
                    int rest = memValue & (0xFFFFFFFF - mask);
                    memValue = rest + (((memValue & mask) - targetSymbol.get().address.get()) & mask);
                } else
                    throw new RuntimeException("Not valid modification operator : " + record.charAt(9));

                // 계산 후 다시 메모리에 쓰기
                memory.setMemValue(startLocationAbsolute, (halfByteLen + 1) / 2, memValue);
            }
        }
        return length;
    }

    private static class ExternalSymbolTable {
        private final List<Symbol> symbolList = new ArrayList<>();

        // csect name이나 symbol name이 같은 심볼을 반환합니다.
        // 존재하지 않으면 empty 반환
        Optional<Symbol> searchByName(String name) {
            for (Symbol sym : symbolList) {
                if (sym.symbolName.isPresent() && sym.symbolName.get().equals(name)) {
                    return Optional.of(sym);
                }
                if (sym.controlSectionName.isPresent() && sym.controlSectionName.get().equals(name)) {
                    return Optional.of(sym);
                }
            }
            return Optional.empty();
        }

        void addSymbol(Symbol symbol) {
            if (symbol.controlSectionName.isPresent()) {
                String name = symbol.controlSectionName.get();
                for (Symbol sym : symbolList)
                    if (sym.controlSectionName.isPresent() && sym.controlSectionName.get().equals(name))
                        throw new RuntimeException("Duplicated controlSection name : " + name);
            }
            if (symbol.symbolName.isPresent()) {
                String name = symbol.symbolName.get();
                for (Symbol sym : symbolList)
                    if (sym.symbolName.isPresent() && sym.symbolName.get().equals(name))
                        throw new RuntimeException("Duplicated symbol name : " + name);
            }
            symbolList.add(symbol);
        }
    }

    private static class Symbol {
        Optional<String> controlSectionName = Optional.empty();
        Optional<String> symbolName = Optional.empty();
        Optional<Integer> address = Optional.empty();
        Optional<Integer> length = Optional.empty();

        void setSymbolName(String symbolName) {
            this.symbolName = Optional.of(symbolName);
        }

        void setCsectName(String csectName) {
            this.controlSectionName = Optional.of(csectName);
        }

        void setAddress(int address) {
            this.address = Optional.of(address);
        }

        void setLength(int length) {
            this.length = Optional.of(length);
        }
    }

    // test
    public static void main(String[] args) throws IOException {
//        relocation_loader_test();
//        linking_loader_test();
//        object_file_load_test();
        String m = "000000B410B400B44077201FE3201B332FFADB2015A00433200957900000B8503B2FE9131000004F0000F1000000B41077100000E32012332FFA53900000DF2008B8503B2FEE4F000005";
        for (int i = 0; i * 8 + 8 <= m.length(); i++) {
            System.out.print(m.substring(i * 8, i * 8 + 8) + " ");
            if (i % 4 == 3) System.out.println();
        }
    }

    private static void relocation_loader_test() {
        List<String> absProgram = new ArrayList<>() {
            {
                add("HCOPY  00000000107A");
                add("T0000001E1410334820390010362810303010154820613C100300102A0C103900102D");
                add("T00001E150C10364820610810334C0000454F46000003000000");
                add("T0010571C1010364C0000F1001000041030E02079302064509039DC20792C1036");
                add("T001073073820644C000005");
                add("E000000");
            }
        };

        Memory memory = new Memory();
        Loader loader = new Loader();
        loader.loadProgram(memory, absProgram, 10, new ExternalSymbolTable());
        System.out.println(memory.getMemString(10, Integer.parseInt("107A", 16) + 10));
    }

    private static void linking_loader_test() {
        List<String> program1 = new ArrayList<>() {
            {
                add("HPROGA 000000000030");
                add("DLISTA 000040ENDA  000054");
                add("RLISTB ENDB  ");
                add("M00000A05+LISTB");
                add("M00000A05-ENDB");
                add("M00002006+PROGA");
                add("E000000");
            }
        };
        List<String> program2 = new ArrayList<>() {
            {
                add("HPROGB 000000000030");
                add("DLISTB 000060ENDB  000070");
                add("RLISTA ENDA  ");
                add("M00000505+LISTA");
                add("M00000505-ENDA");
                add("M00000A06+ENDA");
                add("E");
            }
        };
        List<List<String>> programs = new ArrayList<>() {
            {
                add(program1);
                add(program2);
            }
        };

        Memory memory = new Memory();
        Loader loader = new Loader();
        loader.loadAll(memory, programs, 0);

        String mem = memory.getMemString(0, 100);
        for (int i = 0; i * 8 + 8 <= mem.length(); i++) {
            System.out.print(mem.substring(i * 8, i * 8 + 8) + " ");
            if (i % 4 == 3) System.out.println();
        }
    }

    private static void object_file_load_test() throws IOException {
        Memory memory = new Memory();
        Loader loader = new Loader();
        loader.loadFromFile(memory, "obj.obj", 0);

        String mem = memory.getMemString(0, 4400);
        for (int i = 0; i * 8 + 8 <= mem.length(); i++) {
            System.out.print(mem.substring(i * 8, i * 8 + 8) + " ");
            if (i % 4 == 3) System.out.println();
        }
    }
}
