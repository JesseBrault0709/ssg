package com.jessebrault.gcp.tokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Supplier;

final class DollarScriptletMatcher implements FsmFunction {

    private static final Logger logger = LoggerFactory.getLogger(DollarScriptletMatcher.class);

    private static final class DollarScriptletMatcherOutput implements FsmOutput {

        private final CharSequence entire;
        private final String scriptlet;

        public DollarScriptletMatcherOutput(
                String entire,
                String scriptlet
        ) {
            this.entire = entire;
            this.scriptlet = scriptlet;
        }

        @Override
        public CharSequence entire() {
            return this.entire;
        }

        @Override
        public CharSequence part(int index) {
            return switch (index) {
                case 1 -> "$";
                case 2 -> "{";
                case 3 -> this.scriptlet;
                case 4 -> "}";
                default -> throw new IllegalArgumentException();
            };
        }

    }

    private enum State {
        NO_STRING, G_STRING, SINGLE_QUOTE_STRING
    }

    private static final class CharSequenceIterator implements Iterator<String> {

        private final CharSequence input;
        private int cur;

        public CharSequenceIterator(CharSequence input) {
            this.input = input;
        }

        @Override
        public boolean hasNext() {
            return this.cur < input.length();
        }

        @Override
        public String next() {
            final var c = String.valueOf(input.charAt(this.cur));
            this.cur++;
            return c;
        }

    }

    @Override
    public FsmOutput apply(CharSequence s) {
        final Deque<State> stateStack = new LinkedList<>();
        final Deque<Counter> counterStack = new LinkedList<>();

        final Supplier<Counter> currentCounterSupplier = () -> {
            final var currentCounter = counterStack.peek();
            if (currentCounter == null) {
                throw new IllegalStateException("currentCounter is null");
            }
            return currentCounter;
        };

        stateStack.push(State.NO_STRING);
        counterStack.push(new Counter());

        final Iterator<String> iterator = new CharSequenceIterator(s);

        final var entireAcc = new StringBuilder();

        if (!iterator.hasNext() || !iterator.next().equals("$")) {
            return null;
        } else {
            entireAcc.append("$");
        }

        if (!iterator.hasNext() || !iterator.next().equals("{")) {
            return null;
        } else {
            entireAcc.append("{");
            currentCounterSupplier.get().increment();
        }

        outer:
        while (iterator.hasNext()) {
            if (stateStack.isEmpty()) {
                throw new IllegalStateException("stateStack is empty");
            }
            if (counterStack.isEmpty()) {
                throw new IllegalStateException("counterStack is empty");
            }

            final var c0 = iterator.next();
            entireAcc.append(c0);

            logger.debug("----");
            logger.debug("c0: {}", c0);

            if (stateStack.peek() == State.NO_STRING) {
                switch (c0) {
                    case "{" -> currentCounterSupplier.get().increment();
                    case "}" -> {
                        final var currentCounter = currentCounterSupplier.get();
                        currentCounter.decrement();
                        if (currentCounter.isZero()) {
                            if (counterStack.size() == 1) {
                                logger.debug("last Counter is zero; breaking while loop");
                                break outer;
                            } else {
                                logger.debug("counterStack.size() is greater than 1 and top Counter is zero; " +
                                        "popping state and counter stacks.");
                                stateStack.pop();
                                counterStack.pop();
                            }
                        }
                    }
                    case "\"" -> stateStack.push(State.G_STRING);
                    case "'" -> stateStack.push(State.SINGLE_QUOTE_STRING);
                }
            } else if (stateStack.peek() == State.G_STRING) {
                switch (c0) {
                    case "\\" -> {
                        if (iterator.hasNext()) {
                            final var c1 = iterator.next();
                            entireAcc.append(c1);
                        } else {
                            throw new IllegalArgumentException(
                                    "Ill-formed dollarScriptlet (backslash followed by nothing)"
                            );
                        }
                    }
                    case "$" -> {
                        if (iterator.hasNext()) {
                            final var c1 = iterator.next();
                            entireAcc.append(c1);
                            if (c1.equals("{")) {
                                stateStack.push(State.NO_STRING);
                                counterStack.push(new Counter());
                                currentCounterSupplier.get().increment();
                            }
                        } else {
                            throw new IllegalArgumentException("Ill-formed dollarScriptlet (ends with a dollar)");
                        }
                    }
                    case "\"" -> {
                        logger.debug("popping G_STRING state");
                        stateStack.pop();
                    }
                }
            } else if (stateStack.peek() == State.SINGLE_QUOTE_STRING) {
                switch (c0) {
                    case "\\" -> {
                        if (iterator.hasNext()) {
                            entireAcc.append(iterator.next());
                        } else {
                            throw new IllegalArgumentException(
                                    "Ill-formed dollarScriptlet (backslash followed by nothing)"
                            );
                        }
                    }
                    case "'" -> {
                        logger.debug("popping SINGLE_QUOTE_STRING state");
                        stateStack.pop();
                    }
                }
            } else {
                throw new IllegalStateException(
                        "stateStack contains something which does not equal a state or is null"
                );
            }

            logger.debug("entireAcc: {}", entireAcc);
            logger.debug("stateStack: {}", stateStack);
            logger.debug("counterStack: {}", counterStack);
        }

        return new DollarScriptletMatcherOutput(
                entireAcc.toString(),
                entireAcc.substring(2, entireAcc.length() - 1)
        );
    }

}
