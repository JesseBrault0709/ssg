package com.jessebrault.gcp.groovy

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DollarScriptletParser {

    private static Logger logger = LoggerFactory.getLogger(DollarScriptletParser)

    static class Result {
        String fullMatch
        String scriptlet
    }

    private enum State {
        NO_STRING, G_STRING, SINGLE_QUOTE_STRING
    }

    private static class Counter {

        int count = 0

        void increment() {
            this.count++
        }

        void decrement() {
            this.count--
        }

        void next() {
            this.increment()
        }

        void previous() {
            this.decrement()
        }

        boolean isZero() {
            this.count == 0
        }

        @Override
        String toString() {
            "Counter(${ this.count })"
        }

    }

    static String parse(String input) {
        def acc = new StringBuilder()
        def stateStack = new LinkedList<State>([State.NO_STRING])
        def counterStack = new LinkedList<Counter>([new Counter()])
        def iterator = input.iterator() as Iterator<String>

        if (!iterator.hasNext() || iterator.next() != '$') {
            return null
        } else {
            acc << '$'
            counterStack.peek()++
        }

        if (!iterator.hasNext() || iterator.next() != '{') {
            return null
        } else {
            acc << '{'
        }

        while (iterator.hasNext()) {
            assert counterStack.size() > 0
            assert stateStack.size() > 0

            def c0 = iterator.next()
            acc << c0
            logger.debug('----')
            logger.debug('c0: {}', c0)
            logger.debug('acc: {}', acc)
            if (stateStack.peek() == State.NO_STRING) {
                if (c0 == '{') {
                    counterStack.peek()++
                } else if (c0 == '}') {
                    counterStack.peek()--
                    if (counterStack.peek().isZero()) {
                        if (counterStack.size() == 1) {
                            logger.debug('single Counter is zero; breaking while loop')
                            break // escape while loop
                        } else {
                            logger.debug('counterStack.size() is greater than zero and top Counter is zero; ' +
                                    'popping state and counter stacks')
                            counterStack.pop()
                            stateStack.pop()
                        }
                    }
                } else if (c0 == '"') {
                    stateStack.push(State.G_STRING)
                } else if (c0 == "'") {
                    stateStack.push(State.SINGLE_QUOTE_STRING)
                }
            } else if (stateStack.peek() == State.G_STRING) {
                if (c0 == '\\') {
                    if (iterator.hasNext()) {
                        acc << iterator.next()
                    } else {
                        throw new IllegalArgumentException('Ill-formed dollar groovy')
                    }
                } else if (c0 == '$') {
                    if (iterator.hasNext()) {
                        def c1 = iterator.next()
                        acc << c1
                        if (c1 == '{') {
                            stateStack.push(State.NO_STRING)
                            counterStack.push(new Counter())
                            counterStack.peek()++
                        }
                    } else {
                        throw new IllegalArgumentException('Ill-formed dollar groovy')
                    }
                } else if (c0 == '"') {
                    logger.debug('popping G_STRING state')
                    stateStack.pop()
                }
            } else if (stateStack.peek() == State.SINGLE_QUOTE_STRING) {
                if (c0 == "'") {
                    logger.debug('popping SINGLE_QUOTE_STRING state')
                    stateStack.pop()
                }
            }
            logger.debug('stateStack: {}', stateStack)
            logger.debug('counterStack: {}', counterStack)
        }
        acc.toString()
    }

    static Result parseResult(String input) {
        def match = parse(input)
        if (match) {
            new Result().tap {
                fullMatch = match
                scriptlet = fullMatch.substring(2, fullMatch.length() - 1)
            }
        } else {
            null
        }
    }

}
