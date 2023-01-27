package com.jessebrault.gcp.tokenizer;

import com.jessebrault.fsm.function.FunctionFsm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;

public final class TokenizerImpl implements Tokenizer {

    private static final Logger logger = LoggerFactory.getLogger(TokenizerImpl.class);

    private CharSequence input;
    private int currentOffset;
    private int endOffset;

    private Queue<Token> tokens;
    private FunctionFsm<CharSequence, State, FsmOutput> fsm;

    @Override
    public void start(CharSequence input, int startOffset, int endOffset, State initialState) {
        this.input = input;
        this.currentOffset = startOffset;
        this.endOffset = endOffset;
        this.tokens = new LinkedList<>();
        this.fsm = TokenizerFsm.get(new Accumulator(this.tokens), initialState);
    }

    @Override
    public boolean hasNext() {
        if (this.tokens.isEmpty()) {
            this.getNextTokens();
        }
        return !this.tokens.isEmpty();
    }

    private void getNextTokens() {
        if (this.currentOffset != this.endOffset) {
            final var match = this.fsm.apply(this.input.subSequence(this.currentOffset, this.endOffset));
            if (match == null) {
                logger.error("match is null!");
            } else {
                this.currentOffset += match.entire().length();
            }
        }
    }

    @Override
    public Token next() {
        if (this.tokens.isEmpty()) {
            throw new IllegalStateException("currentAccumulatedTokens is empty");
        }
        return this.tokens.remove();
    }

    @Override
    public State getCurrentState() {
        return this.fsm.getCurrentState();
    }

}
