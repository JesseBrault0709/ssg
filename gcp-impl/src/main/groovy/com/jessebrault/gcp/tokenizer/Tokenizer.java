package com.jessebrault.gcp.tokenizer;

import java.util.Queue;

public final class Tokenizer {

    public static Queue<Token> tokenize(final String gcpSrc) {
        final var acc = new Accumulator();
        final var fsm = TokenizerFsm.get(acc);

        String remaining = gcpSrc;
        while (remaining.length() > 0) {
            final var o = fsm.apply(remaining);
            if (o == null) {
                throw new IllegalStateException();
            }
            remaining = remaining.substring(o.entire().length());
        }

        return acc.getTokens();
    }

}
