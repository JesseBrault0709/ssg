package com.jessebrault.gcp.component

import com.jessebrault.fsm.function.FunctionFsmBuilder
import com.jessebrault.fsm.function.FunctionFsmBuilderImpl
import com.jessebrault.gcp.groovy.DollarScriptletParser
import com.jessebrault.gcp.util.PatternFunction

import static ComponentToken.Type

class ComponentTokenizer {

    private static final PatternFunction lessThan = new PatternFunction(~/^</)
    private static final PatternFunction greaterThan = new PatternFunction(~/^>/)
    private static final PatternFunction identifier = new PatternFunction(~/^\p{Lu}.*?(?=\s|\/)/)
    private static final PatternFunction whitespace = new PatternFunction(~/^\s+/)
    private static final PatternFunction key = new PatternFunction(~/^[\p{L}0-9_\-]+/)
    private static final PatternFunction equals = new PatternFunction(~/^=/)
    private static final PatternFunction doubleQuote = new PatternFunction(~/^"/)
    private static final PatternFunction doubleQuoteStringContent = new PatternFunction(~/^(?:[\w\W&&[^\\"]]|\\")+/)
    private static final PatternFunction singleQuote = new PatternFunction(~/^'/)
    private static final PatternFunction singleQuoteStringContent = new PatternFunction(~/^(?:[\w\W&&[^\\']]|\\')+/)

    // https://docs.groovy-lang.org/latest/html/documentation/#_identifiers
    //'\u00C0' to '\u00D6'
    //'\u00D8' to '\u00F6'
    //'\u00F8' to '\u00FF'
    //'\u0100' to '\uFFFE'
    private static final PatternFunction dollarReference = new PatternFunction(~/^\$[a-zA-Z_$\u00c0-\u00d6\u00d8-\u00f6\u00f8-\u00ff\u0100-\ufff3][a-zA-Z_$0-9\u00c0-\u00d6\u00d8-\u00f6\u00f8-\u00ff\u0100-\ufff3]*(?=[\s\/>])/)

    private static final PatternFunction percent = new PatternFunction(~/^%/)
    private static final PatternFunction expressionScriptletGroovy = new PatternFunction(~/^.*?%>/)
    private static final PatternFunction forwardSlash = new PatternFunction(~/^\//)

    static enum State {
        START,
        IDENTIFIER,
        KEYS_AND_VALUES,
        DOUBLE_QUOTE_STRING,
        SINGLE_QUOTE_STRING,
        EXPRESSION_SCRIPTLET_GROOVY,
        DONE
    }

    private static FunctionFsmBuilder<String, State, String> getFsmBuilder() {
        new FunctionFsmBuilderImpl<>()
    }

    Queue<ComponentToken> tokenize(String src) {
        Queue<ComponentToken> tokens = new LinkedList<>()

        def fsm = getFsmBuilder().with {
            initialState = State.START

            whileIn(State.START) {
                on lessThan shiftTo State.IDENTIFIER exec {
                    tokens << new ComponentToken(Type.LT, it)
                }
                onNoMatch() exec {
                    throw new IllegalArgumentException()
                }
            }

            whileIn(State.IDENTIFIER) {
                on identifier shiftTo State.KEYS_AND_VALUES exec {
                    tokens << new ComponentToken(Type.IDENTIFIER, it)
                }
                onNoMatch() exec {
                    throw new IllegalArgumentException()
                }
            }

            whileIn(State.KEYS_AND_VALUES) {
                on greaterThan shiftTo State.DONE exec {
                    tokens << new ComponentToken(Type.GT, it)
                }
                on whitespace exec { }
                on key exec {
                    tokens << new ComponentToken(Type.KEY, it)
                }
                on equals exec {
                    tokens << new ComponentToken(Type.EQUALS, it)
                }
                on doubleQuote shiftTo State.DOUBLE_QUOTE_STRING exec {
                    tokens << new ComponentToken(Type.DOUBLE_QUOTE, it)
                }
                on singleQuote shiftTo State.SINGLE_QUOTE_STRING exec {
                    tokens << new ComponentToken(Type.SINGLE_QUOTE, it)
                }
                on dollarReference exec { String s ->
                    tokens << new ComponentToken(Type.GROOVY_IDENTIFIER, s.substring(1)) // skip opening $
                }
                //noinspection GroovyAssignabilityCheck // for some reason IntelliJ is confused by this
                on DollarScriptletParser::parse exec { String s ->
                    tokens << new ComponentToken(Type.GROOVY, s.substring(2, s.length() - 1))
                }
                on percent shiftTo State.EXPRESSION_SCRIPTLET_GROOVY exec {
                    tokens << new ComponentToken(Type.PERCENT, it)
                }
                on forwardSlash exec {
                    tokens << new ComponentToken(Type.FORWARD_SLASH, it)
                }
                onNoMatch() exec {
                    throw new IllegalArgumentException()
                }
            }

            whileIn(State.DOUBLE_QUOTE_STRING) {
                on doubleQuoteStringContent exec {
                    tokens << new ComponentToken(Type.STRING, it)
                }
                on doubleQuote shiftTo State.KEYS_AND_VALUES exec {
                    tokens << new ComponentToken(Type.DOUBLE_QUOTE, it)
                }
                onNoMatch() exec {
                    throw new IllegalArgumentException()
                }
            }

            whileIn(State.SINGLE_QUOTE_STRING) {
                on singleQuoteStringContent exec {
                    tokens << new ComponentToken(Type.STRING, it)
                }
                on singleQuote shiftTo State.KEYS_AND_VALUES exec {
                    tokens << new ComponentToken(Type.SINGLE_QUOTE, it)
                }
                onNoMatch() exec {
                    throw new IllegalArgumentException()
                }
            }

            whileIn(State.EXPRESSION_SCRIPTLET_GROOVY) {
                on expressionScriptletGroovy shiftTo State.KEYS_AND_VALUES exec { String s ->
                    tokens << new ComponentToken(Type.GROOVY, s.substring(0, s.length() - 2))
                    tokens << new ComponentToken(Type.PERCENT, '%')
                    tokens << new ComponentToken(Type.GT, '>')
                }
                onNoMatch() exec {
                    throw new IllegalArgumentException()
                }
            }

            build()
        }

        def remaining = src

        while (fsm.currentState != State.DONE) {
            def output = fsm.apply(remaining)
            if (!output) {
                throw new IllegalStateException()
            } else {
                remaining = remaining.substring(output.length())
            }
        }

        tokens
    }

}
