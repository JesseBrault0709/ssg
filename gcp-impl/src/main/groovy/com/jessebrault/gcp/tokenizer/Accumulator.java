package com.jessebrault.gcp.tokenizer;

import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Pattern;

final class Accumulator {

    private static final Pattern newline = Pattern.compile("([\n\r])");

    private final Queue<Token> tokens = new LinkedList<>();
    private int line = 1;
    private int col = 1;

    public void accumulate(Token.Type type, String text) {
        this.tokens.add(new Token(type, text, this.line, this.col));
        final var m = newline.matcher(text);
        if (m.find()) {
            this.line += m.groupCount();
            this.col = 1;
        } else {
            this.col += text.length();
        }
    }

    public Queue<Token> getTokens() {
        return this.tokens;
    }

}
