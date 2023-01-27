package com.jessebrault.gcp.tokenizer;

import java.util.regex.MatchResult;
import java.util.regex.Pattern;

final class PatternMatcher implements FsmFunction {

    private static final class MatchResultFsmOutput implements FsmOutput {

        private final MatchResult matchResult;

        public MatchResultFsmOutput(MatchResult matchResult) {
            this.matchResult = matchResult;
        }

        @Override
        public CharSequence entire() {
            return this.matchResult.group(0);
        }

        @Override
        public CharSequence part(int index) {
            return this.matchResult.group(index);
        }

        @Override
        public String toString() {
            return "MatchResultFsmOutput(" + this.entire() + ")";
        }

    }

    private final Pattern pattern;

    public PatternMatcher(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public FsmOutput apply(CharSequence s) {
        final var m = this.pattern.matcher(s);
        return m.find() ? new MatchResultFsmOutput(m) : null;
    }

    @Override
    public String toString() {
        return "MatcherFunction(" + this.pattern + ")";
    }

}
