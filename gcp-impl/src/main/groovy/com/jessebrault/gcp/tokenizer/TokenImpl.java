package com.jessebrault.gcp.tokenizer;

public final class TokenImpl implements Token {

    private final Type type;
    private final CharSequence text;
    private final int inputIndex;
    private final int line;
    private final int col;

    public TokenImpl(Type type, CharSequence text, int inputIndex, int line, int col) {
        this.type = type;
        this.text = text;
        this.inputIndex = inputIndex;
        this.line = line;
        this.col = col;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public CharSequence getText() {
        return text;
    }

    @Override
    public int getInputIndex() {
        return 0;
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public int getCol() {
        return col;
    }

    @Override
    public String toString() {
        return String.format("Token(%s, %s, %d, %d, %d)", this.type, this.text, this.inputIndex, this.line, this.col);
    }

}
