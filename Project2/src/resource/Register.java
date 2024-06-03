package resource;

public class Register {
    // 일단 F 레지스터는 생각하지 않음
    private final int[] bytes = new int[3];

    public void setValue(int value) {
        for (int i = 0; i < 3; i++) {
            this.bytes[i] = (value >> (8 * (2 - i))) & 0xFF;
        }
    }

    public int getValue() {
        int ret = 0;
        for (int i = 0; i < 3; i++)
            ret |= (bytes[i] & 0xFF) << (8 * (2 - i));
        return ret;
    }

    public void add(int value) {
        int val = getValue();
        setValue(val + value);
    }

    public void sub(int value) {
        int val = getValue();
        setValue(val - value);
    }

    public void mul(int value) {
        int val = getValue();
        setValue(val * value);
    }

    public void div(int value) {
        int val = getValue();
        setValue(val / value);
    }

    // test
    public static void main(String[] args) {
        Register r = new Register();
        System.out.println(r.getValue()); // 0
        r.setValue(123);
        System.out.println(r.getValue()); // 123
        r.add(10);
        System.out.println(r.getValue()); // 133
    }
}
