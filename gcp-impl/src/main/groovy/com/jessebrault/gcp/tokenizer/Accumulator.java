package com.jessebrault.gcp.tokenizer;

import java.util.Queue;
import java.util.regex.Pattern;

final class Accumulator {

    private static final Pattern newline = Pattern.compile("([\n\r])");

    private final Queue<Token> tokens;
    private int inputIndex = 0;
    private int line = 1;
    private int col = 1;

    public Accumulator(Queue<Token> tokenQueue) {
        this.tokens = tokenQueue;
    }

    public void accumulate(Token.Type type, CharSequence text) {
        this.tokens.add(new TokenImpl(type, text, this.inputIndex, this.line, this.col));
        this.inputIndex += text.length();
        final var m = newline.matcher(text);
        if (m.find()) {
            this.line += m.groupCount();
            this.col = 1;
        } else {
            this.col += text.length();
        }
    }

}
