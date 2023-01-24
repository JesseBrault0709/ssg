package com.jessebrault.gcp.tokenizer;

import java.util.function.Function;
import java.util.regex.Pattern;

final class PatternMatcher implements Function<String, FsmOutput> {

    private final Pattern pattern;

    public PatternMatcher(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public FsmOutput apply(String s) {
        final var m = this.pattern.matcher(s);
        return m.find() ? new MatchResultFsmOutput(m) : null;
    }

    @Override
    public String toString() {
        return "MatcherFunction(" + this.pattern + ")";
    }

}
