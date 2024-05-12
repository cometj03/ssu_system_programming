package numeric;

public class Numeric {

    public Numeric(int value) {
        // int의 앞 3바이트만 다룹니다
        // value < 2^24
        this.size = 3;
        this.bytes = new byte[3];
        unpack(value);
    }

    /**
     * 리터럴 처리 (=123, =C'EOF', =X'05')
     * WORD, BYTE 값 처리 (123, C'EOF', X'05')
     *
     * @throws RuntimeException 정수 파싱 실패
     */
    public Numeric(String str) throws RuntimeException {
        if (str.startsWith("=")) str = str.substring(1);
        switch (str.charAt(0)) {
            case 'C':
                str = str.substring(2, str.length() - 1);
                this.size = str.length();
                this.bytes = new byte[this.size];
                for (int i = 0; i < this.size; i++) {
                    this.bytes[i] = (byte) str.charAt(i);
                }
                break;
            case 'X':
                str = str.substring(2, str.length() - 1);
                if (str.length() % 2 == 1) str = "0" + str; // 길이가 홀수일 떄 맨 앞에 0 추가
                this.size = str.length() / 2;
                this.bytes = new byte[this.size];
                try {
                    for (int i = 0; i < this.size; i++) {
                        this.bytes[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
                    }
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Failed to parse byte(base 16) : " + str + e.getMessage());
                }
                break;
            default:
                this.size = 3;
                this.bytes = new byte[3];
                try {
                    int num = Integer.parseInt(str);
                    unpack(num);
                } catch (NumberFormatException e) {
                    unpack(0);
                    throw new RuntimeException("Failed to parse int : " + str);
                }
        }
    }

    private void unpack(int value) {
        if (this.bytes.length < 3) return;
        for (int i = 2; i >= 0; i--) {
            this.bytes[i] = (byte) (value % (1 << 8));
            value >>= 8;
        }
    }

    // bytes를 16진수 문자열로 변환한 값을 리턴합니다.
    public String packValue() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            builder.append(String.format("%02X", bytes[i]));
        }
        return builder.toString();
    }

    public int getSize() {
        return size;
    }

    private final byte[] bytes;
    private final int size;
}
