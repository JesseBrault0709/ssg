package com.jessebrault.gcp.tokenizer;

import java.util.Collection;

public final class Token {

    public enum Type {
        TEXT,

        DOLLAR,
        GROOVY_REFERENCE,
        CURLY_OPEN,
        SCRIPTLET,
        CURLY_CLOSE,
        BLOCK_SCRIPTLET_OPEN,
        EXPRESSION_SCRIPTLET_OPEN,
        SCRIPTLET_CLOSE,

        CLASS_NAME,
        PACKAGE_NAME,
        DOT,

        WHITESPACE,

        KEY,
        EQUALS,

        DOUBLE_QUOTE,
        STRING,
        SINGLE_QUOTE,

        COMPONENT_START,
        FORWARD_SLASH,
        COMPONENT_END,
        ;

        boolean isAnyOf(Collection<Type> types) {
            return types.contains(this);
        }

    }

    private final Type type;
    private final String text;
    private final int line;
    private final int col;

    public Token(Type type, String text, int line, int col) {
        this.type = type;
        this.text = text;
        this.line = line;
        this.col = col;
    }

    public Type getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public int getLine() {
        return line;
    }

    public int getCol() {
        return col;
    }

    @Override
    public String toString() {
        return String.format("Token(%s, %s, %d, %d)", this.type, this.text, this.line, this.col);
    }

}
