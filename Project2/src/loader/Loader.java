package loader;

import resource.Memory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Object code를 SIC/XE 가상 머신의 메모리에 로드하는 모듈
 * <br/>
 * SIC/XE object code를 파싱하고, 파싱한 정보를 토대로 ResourceManager 내에
 * 변수로 지정되어 있는 가상의 메모리 영역에 로드함
 */
public class Loader {

    public void load(Memory memory, String objFileName) throws IOException {
        List<String> lines = new ArrayList<>();
        File file = new File(objFileName);
        BufferedReader bufReader = new BufferedReader(new FileReader(file));

        String line;
        while ((line = bufReader.readLine()) != null) {
            lines.add(line);
        }
        bufReader.close();

        load(memory, lines);
    }

    private void load(Memory memory, List<String> lines) {

    }

    static class ExternalSymbolTable {

    }

    static class Symbol {
        int absoluteAddr;
    }
}
