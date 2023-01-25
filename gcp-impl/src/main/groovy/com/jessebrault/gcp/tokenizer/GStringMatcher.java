package com.jessebrault.gcp.tokenizer;

import com.jessebrault.fsm.stackfunction.StackFunctionFsm;
import com.jessebrault.fsm.stackfunction.StackFunctionFsmBuilder;
import com.jessebrault.fsm.stackfunction.StackFunctionFsmBuilderImpl;

import java.util.function.Function;
import java.util.regex.Pattern;

final class GStringMatcher implements Function<String, FsmOutput> {

    private static final class GStringMatcherOutput implements FsmOutput {

        private final String entire;
        private final String contents;

        public GStringMatcherOutput(String entire, String contents) {
            this.entire = entire;
            this.contents = contents;
        }

        @Override
        public String entire() {
            return this.entire;
        }

        @Override
        public String part(int index) {
            return switch(index) {
                case 1, 3 -> "\"";
                case 2 -> this.contents;
                default -> throw new IllegalArgumentException();
            };
        }

    }

    private static final PatternMatcher text = new PatternMatcher(
            Pattern.compile("^(?:[\\w\\W&&[^$\\\\\"\\n\\r]]|\\\\[\"nrbfst\\\\u]|\\$(?!\\{|[\\w$]+(?:\\.[\\w$]+)*))+")
    );
    private static final DollarScriptletMatcher dollarScriptlet = new DollarScriptletMatcher();
    private static final PatternMatcher doubleQuote = new PatternMatcher(
            Pattern.compile("^\"")
    );

    private enum State {
        START, CONTENTS, DONE
    }

    private static StackFunctionFsmBuilder<String, State, FsmOutput> getFsmBuilder() {
        return new StackFunctionFsmBuilderImpl<>();
    }

    private static StackFunctionFsm<String, State, FsmOutput> getFsm(StringBuilder acc) {
        return getFsmBuilder()
                .setInitialState(State.START)
                .whileIn(State.START, sc -> {
                    sc.on(doubleQuote).shiftTo(State.CONTENTS).exec(o -> {
                        acc.append(o.entire());
                    });
                    sc.onNoMatch().exec(input -> {
                        throw new IllegalArgumentException();
                    });
                })
                .whileIn(State.CONTENTS, sc -> {
                    sc.on(text).exec(o -> {
                        acc.append(o.entire());
                    });
                    sc.on(dollarScriptlet).exec(o -> {
                        acc.append(o.entire());
                    });
                    sc.on(doubleQuote).shiftTo(State.DONE).exec(o -> {
                        acc.append(o.entire());
                    });
                    sc.onNoMatch().exec(input -> {
                        throw new IllegalArgumentException();
                    });
                })
                .build();
    }

    @Override
    public FsmOutput apply(final String s) {
        final var acc = new StringBuilder();
        final var fsm = getFsm(acc);

        String remaining = s;

        // Look-ahead
        if (!remaining.startsWith("\"")) {
            return null;
        }

        while (remaining.length() > 0) {
            final var output = fsm.apply(remaining);
            if (output == null) {
                throw new IllegalStateException("output is null");
            }
            if (fsm.getCurrentState() == State.DONE) {
                break;
            }
            remaining = remaining.substring(output.entire().length());
        }

        final var entire = acc.toString();
        return new GStringMatcherOutput(entire, entire.substring(1, entire.length() - 1));
    }

}
