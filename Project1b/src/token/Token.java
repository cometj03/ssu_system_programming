package token;

public abstract class Token {

    public Token(int address, int size) {
        this.address = address;
        this.size = size;
    }

    public int getAddress() {
        return address;
    }

    public int getSize() {
        return size;
    }

    private final int address;
    private final int size;
}
