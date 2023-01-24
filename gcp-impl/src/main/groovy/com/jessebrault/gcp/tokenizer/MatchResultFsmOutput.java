package com.jessebrault.gcp.tokenizer;

import java.util.regex.MatchResult;

public class MatchResultFsmOutput implements FsmOutput {

    private final MatchResult matchResult;

    public MatchResultFsmOutput(MatchResult matchResult) {
        this.matchResult = matchResult;
    }

    @Override
    public String entire() {
        return this.matchResult.group();
    }

    @Override
    public String part(int index) {
        return this.matchResult.group(index);
    }

}
