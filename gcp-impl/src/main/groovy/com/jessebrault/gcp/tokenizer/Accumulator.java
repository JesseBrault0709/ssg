package com.jessebrault.gcp.tokenizer;

import java.util.LinkedList;
import java.util.Queue;

final class Accumulator {

    Queue<Token> tokens = new LinkedList<>();
    int line = 1;
    int col = 1;

    public void accumulate(Token.Type type, String text) {
        this.tokens.add(new Token(type, text, this.line, this.col));
        if (type == Token.Type.NEWLINE) {
            this.line++;
            this.col = 1;
        } else {
            this.col += text.length();
        }
    }

}
