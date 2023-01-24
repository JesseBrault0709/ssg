package com.jessebrault.gcp.tokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Function;

/**
 * NOT THREAD SAFE
 */
final class DollarScriptletMatcher implements Function<String, FsmOutput> {

    private static final Logger logger = LoggerFactory.getLogger(DollarScriptletMatcher.class);

    private static final class DollarScriptletMatcherOutput implements FsmOutput {

        private final String entire;
        private final String dollar;
        private final String openingCurly;
        private final String scriptlet;
        private final String closingCurly;

        public DollarScriptletMatcherOutput(
                String entire,
                String dollar,
                String openingCurly,
                String scriptlet,
                String closingCurly
        ) {
            this.entire = entire;
            this.dollar = dollar;
            this.openingCurly = openingCurly;
            this.scriptlet = scriptlet;
            this.closingCurly = closingCurly;
        }

        @Override
        public String entire() {
            return this.entire;
        }

        @Override
        public String part(int index) {
            return switch (index) {
                case 1 -> this.dollar;
                case 2 -> this.openingCurly;
                case 3 -> this.scriptlet;
                case 4 -> this.closingCurly;
                default -> throw new IllegalArgumentException();
            };
        }

    }

    private enum State {
        NO_STRING, G_STRING, SINGLE_QUOTE_STRING
    }

    private static final class Counter {

        private int count = 0;

        public void increment() {
            this.count++;
        }

        public void decrement() {
            this.count--;
        }

        public boolean isZero() {
            return this.count == 0;
        }

        @Override
        public String toString() {
            return "Counter(" + this.count + ")";
        }

    }

    private Deque<State> stateStack;
    private Deque<Counter> counterStack;

    private Counter getCurrentCounter() {
        final var currentCounter = this.counterStack.peek();
        if (currentCounter == null) {
            throw new IllegalStateException("currentCounter is null");
        }
        return currentCounter;
    }

    @Override
    public FsmOutput apply(String s) {
        this.stateStack = new LinkedList<>();
        this.counterStack = new LinkedList<>();

        stateStack.push(State.NO_STRING);
        counterStack.push(new Counter());

        final Iterator<String> iterator = new Iterator<>() {

            private int cur;

            @Override
            public boolean hasNext() {
                return this.cur < s.length();
            }

            @Override
            public String next() {
                final var c = String.valueOf(s.charAt(this.cur));
                this.cur++;
                return c;
            }

        };

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
            this.getCurrentCounter().increment();
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
                    case "{" -> this.getCurrentCounter().increment();
                    case "}" -> {
                        final var currentCounter = this.getCurrentCounter();
                        currentCounter.decrement();
                        if (currentCounter.isZero()) {
                            if (this.counterStack.size() == 1) {
                                logger.debug("last Counter is zero; breaking while loop");
                                break outer;
                            } else {
                                logger.debug("counterStack.size() is greater than 1 and top Counter is zero; " +
                                        "popping state and counter stacks.");
                                this.stateStack.pop();
                                this.counterStack.pop();
                            }
                        }
                    }
                    case "\"" -> this.stateStack.push(State.G_STRING);
                    case "'" -> this.stateStack.push(State.SINGLE_QUOTE_STRING);
                }
            } else if (stateStack.peek() == State.G_STRING) {
                switch (c0) {
                    case "\\" -> {
                        if (iterator.hasNext()) {
                            final var c1 = iterator.next();
                            entireAcc.append(c1);
                        } else {
                            throw new IllegalArgumentException("Ill-formed dollarScriptlet (backslash followed by nothing)");
                        }
                    }
                    case "$" -> {
                        if (iterator.hasNext()) {
                            final var c1 = iterator.next();
                            entireAcc.append(c1);
                            if (c1.equals("{")) {
                                this.stateStack.push(State.NO_STRING);
                                this.counterStack.push(new Counter());
                                this.getCurrentCounter().increment();
                            }
                        } else {
                            throw new IllegalArgumentException("Ill-formed dollarScriptlet (ends with a dollar)");
                        }
                    }
                    case "\"" -> {
                        logger.debug("popping G_STRING state");
                        this.stateStack.pop();
                    }
                }
            } else if (stateStack.peek() == State.SINGLE_QUOTE_STRING) {
                switch (c0) {
                    case "\\" -> {
                        if (iterator.hasNext()) {
                            entireAcc.append(iterator.next());
                        } else {
                            throw new IllegalArgumentException("Ill-formed dollarScriptlet (backslash followed by nothing)");
                        }
                    }
                    case "'" -> {
                        logger.debug("popping SINGLE_QUOTE_STRING state");
                        this.stateStack.pop();
                    }
                }
            } else {
                throw new IllegalStateException("stateStack contains something which does not equal a state or is null");
            }

            logger.debug("entireAcc: {}", entireAcc);
            logger.debug("stateStack: {}", stateStack);
            logger.debug("counterStack: {}", counterStack);
        }

        return new DollarScriptletMatcherOutput(
                entireAcc.toString(),
                "$",
                "{",
                entireAcc.substring(2, entireAcc.length() - 1),
                "}"
        );
    }

}
