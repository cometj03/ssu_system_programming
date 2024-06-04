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
        if (bytes > 4) return -1;
        if (startAddr + bytes >= capacity) throw new RuntimeException("Memory capacity exceeded");

        int ret = 0;
        for (int i = 0; i < bytes; i++) {
            ret |= (mem[i + startAddr] & 0xFF) << (8 * (bytes - i - 1));
        }
        return ret;
    }

    public void setMem(int startAddr, String value) {
        if (value.length() % 2 != 0) throw new RuntimeException("setMem");

        for (int i = 0; i < value.length() / 2; i++) {
            mem[i + startAddr] = Integer.parseInt(value.substring(i * 2, i * 2 + 2), 16);
        }
    }

    // test
    public static void main(String[] args) {
        Memory mem = new Memory(100);
        mem.setMem(0, "172027");
        System.out.println(mem.getMemValue(0, 1)); // 23
        System.out.println(mem.getMemValue(1, 1)); // 32
        System.out.println(mem.getMemValue(2, 1)); // 39
        System.out.println(mem.getMemValue(0, 3)); // 1515559

        int val = mem.getMemValue(0, 3);
        mem.setMem(0, String.format("%06X", val + 1));
        System.out.println(mem.getMemValue(0, 3)); // 1515560
    }
}
