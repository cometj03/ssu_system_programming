package resource;

public class Memory {
    private final int capacity;
    private final int[] mem;

    public Memory() {
        this(10000);
    }

    public Memory(int capacity) {
        this.capacity = capacity;
        this.mem = new int[capacity];
    }

    public String getMemString(int startAddr, int bytes) {
        StringBuilder builder = new StringBuilder(bytes * 2);
        for (int i = 0; i < bytes; i++) {
            builder.append(String.format("%02X", mem[i + startAddr]));
        }
        return builder.toString();
    }

    public int getMemValue(int startAddr, int bytes) {
        if (bytes > 3)
            throw new RuntimeException("up to 3 bytes");
        if (startAddr + bytes >= capacity)
            throw new RuntimeException("Memory capacity exceeded");

        int ret = 0;
        for (int i = 0; i < bytes; i++) {
            ret |= (mem[i + startAddr] & 0xFF) << (8 * (bytes - i - 1));
        }
        if (bytes == 3 && (mem[startAddr] & 0x80) != 0) {
            // 1 word의 값의 최상위 바이트가 1인 경우 음수라고 판단
            ret += 0xFF000000;
        }
        return ret;
    }

    public void setMemString(int startAddr, String value) {
        if (value.length() % 2 != 0) throw new RuntimeException("setMem");

        for (int i = 0; i < value.length() / 2; i++) {
            mem[i + startAddr] = Integer.parseInt(value.substring(i * 2, i * 2 + 2), 16);
        }
    }

    public void setMemValue(int startAddr, int bytes, int value) {
        for (int i = 0; i < bytes; i++) {
            mem[i + startAddr] = (value >> (8 * (bytes - i - 1))) & 0xFF;
        }
    }

    // test
    public static void main(String[] args) {
        Memory mem = new Memory(100);
        mem.setMemString(0, "172027");
        System.out.println(mem.getMemValue(0, 1)); // 23
        System.out.println(mem.getMemValue(1, 1)); // 32
        System.out.println(mem.getMemValue(2, 1)); // 39
        System.out.println(mem.getMemValue(0, 3)); // 1515559

        int val = mem.getMemValue(0, 3);
        mem.setMemString(0, String.format("%06X", val + 1));
        System.out.println(mem.getMemValue(0, 3)); // 1515560
        mem.setMemValue(0, 3, val + 1);
        System.out.println(mem.getMemValue(0, 3)); // 1515560

        mem.setMemString(10, "0FFFF0");
        int negVal = mem.getMemValue(10, 3);
        mem.setMemValue(10, 3, negVal);
        System.out.println(negVal); // 0xFFFF0 == 1048560

        // 음수인지 판별
        if ((negVal & 0xFFFFF) >> 19 == 1)
            System.out.println("neg");

        mem.setMemValue(16, 3, -16);
        negVal = mem.getMemValue(16, 3);
        System.out.println(negVal); // -16
    }
}
