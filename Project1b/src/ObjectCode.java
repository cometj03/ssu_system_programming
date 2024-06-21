import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ObjectCode {

    private Optional<String> sectionName = Optional.empty();
    private Optional<Integer> startAddress = Optional.empty();
    private Optional<Integer> programLength = Optional.empty();
    private Optional<Integer> initialPC = Optional.empty();
    private final List<Define> defines = new ArrayList<>();
    private final List<String> refers = new ArrayList<>();
    private final List<Text> texts = new ArrayList<>();
    private final List<Modification> modifications = new ArrayList<>();

    /**
     * ObjectCode 객체를 String으로 변환한다. Assembler.java에서 오브젝트 코드를 출력하는 데에 사용된다.
     */
    @Override
    public String toString() throws RuntimeException {
        if (sectionName.isEmpty() || startAddress.isEmpty() || programLength.isEmpty())
            throw new RuntimeException("illegal operation");

        String header = String.format("H%-6s%06X%06X\n", sectionName.get(), startAddress.get(), programLength.get());

        String refer = this.refers.isEmpty() ? "" : "R" + this.refers.stream()
                .map(r -> String.format("%-6s", r))
                .collect(Collectors.joining()) + "\n";

        String define = this.defines.isEmpty() ? "" : "D" + this.defines.stream()
                .map(d -> String.format("%-6s%06X", d.symbolName, d.address))
                .collect(Collectors.joining()) + "\n";

        String text = this.texts.stream()
                .map(t -> String.format("T%06X%02X%s\n", t.startAddress, t.getLength() / 2, t.getTextContent()))
                .collect(Collectors.joining());

        String modification = this.modifications.stream()
                .map(m -> String.format("M%06X%02X%c%-6s\n",
                        m.startAddress, m.sizeHalfBytes, m.modificationFlag, m.symbol))
                .collect(Collectors.joining());

        String end = "E" + initialPC
                .map(x -> String.format("%06X", x))
                .orElse("");

        return header + refer + define + text + modification + end;
    }

    public ObjectCode() {
    }

    public void setSectionName(String sectionName) {
        this.sectionName = Optional.of(sectionName);
    }

    public void setStartAddress(int startAddress) {
        this.startAddress = Optional.of(startAddress);
    }

    public void setProgramLength(int programLength) {
        this.programLength = Optional.of(programLength);
    }

    public void addDefineSymbol(String symbolName, int addr) {
        defines.add(new Define(symbolName, addr));
    }

    public void addReferSymbol(String symbolName) {
        refers.add(symbolName);
    }

    public void addText(int address, String text) {
        Text lastText;
        if (texts.isEmpty())
            lastText = addTextNewLine(address);
        else
            lastText = texts.get(texts.size() - 1);

        // 아직 내용이 없으면 시작주소 갱신 (RESB, RESW가 연속으로 있을 때를 고려) 
        if (lastText.getLength() == 0) {
            lastText.setStartAddress(address);
        }

        // 길이가 초과되면 새로운 텍스트 추가
        if (lastText.getLength() + text.length() > 60) {
            lastText = addTextNewLine(address);
        }
        lastText.addHexString(text);
    }

    // 새로운 텍스트 레코드를 삽입하고,
    // 새로 삽입된 객체를 반환합니다.
    public Text addTextNewLine(int address) {
        // 비어있거나 마지막 text의 내용이 있을 경우에만 추가
        if (texts.isEmpty() || texts.get(texts.size() - 1).getLength() > 0) {
            Text text = new Text(address);
            texts.add(text);
            return text;
        }
        return null;
    }

    public void addModification(String symbolName, boolean isPlus, int startAddr, int sizeHalfByte) {
        modifications.add(
                new Modification(startAddr, sizeHalfByte, isPlus ? '+' : '-', symbolName));
    }

    public void setInitialPC(int address) {
        initialPC = Optional.of(address);
    }

    public static class Text {
        private int startAddress;
        private int length;
        private final StringBuilder textContent;

        Text(int startAddress) {
            this.startAddress = startAddress;
            this.length = 0;
            this.textContent = new StringBuilder();
        }

        void addByte(byte b) {
            addHexString(String.format("%X", b));
        }

        void addHexString(String hex) {
            length += hex.length();
            textContent.append(hex);
        }

        int getLength() {
            return length;
        }

        void setStartAddress(int startAddress) {
            this.startAddress = startAddress;
        }

        String getTextContent() {
            return textContent.toString();
        }
    }

    static class Modification {
        final int startAddress;
        final int sizeHalfBytes;
        final char modificationFlag; // + or -
        final String symbol;

        Modification(int startAddress, int sizeHalfBytes, char modificationFlag, String symbol) {
            this.startAddress = startAddress;
            this.sizeHalfBytes = sizeHalfBytes;
            this.modificationFlag = modificationFlag;
            this.symbol = symbol;
        }
    }

    static class Define {
        final String symbolName;
        final int address;

        Define(String symbolName, int address) {
            this.symbolName = symbolName;
            this.address = address;
        }
    }
}
