package token;

public abstract class Token {

    public Token(String tokenString, int address, int size) {
        this.tokenString = tokenString;
        this.address = address;
        this.size = size;
    }

    public String getTokenString() {
        return tokenString;
    }

    public int getAddress() {
        return address;
    }

    public int getSize() {
        return size;
    }

    private final String tokenString;
    private final int address;
    private final int size;
}
