package com.jessebrault.gcp

import com.jessebrault.fsm.stackfunction.StackFunctionFsmBuilder
import com.jessebrault.fsm.stackfunction.StackFunctionFsmBuilderImpl
import com.jessebrault.gcp.util.PatternFunction

class GcpToScriptConverterImpl implements GcpToScriptConverter {

    enum State {
        HTML,
        SCRIPTLET,
        EXPRESSION_SCRIPTLET,
        DOLLAR,

        COMPONENT,

        COMPONENT_IDENTIFIER,

        COMPONENT_ATTR_KEY,
        COMPONENT_ATTR_VALUE_OPEN,

        COMPONENT_ATTR_VALUE_STRING,
        COMPONENT_ATTR_VALUE_STRING_CLOSE,

        COMPONENT_CLOSE
    }

    private static final PatternFunction html = new PatternFunction(~/^(?:[\w\W&&[^<$]]|<(?!%|\p{Lu})|\$(?!\{))+/)
    private static final PatternFunction scriptletOpen = new PatternFunction(~/^<%(?!=)/)
    private static final PatternFunction expressionScriptletOpen = new PatternFunction(~/^<%=/)
    private static final PatternFunction scriptletText = new PatternFunction(~/^.+(?=%>)/)
    private static final PatternFunction scriptletClose = new PatternFunction(~/^%>/)

    private static final PatternFunction componentOpen = new PatternFunction(~/^<(?=\p{Lu})/)
    private static final PatternFunction componentIdentifier = new PatternFunction(~/^\p{Lu}.*?(?=\s|\\/)/)
    private static final PatternFunction attrKeyWithValue = new PatternFunction(~/^\s*[\p{Ll}\p{Lu}0-9_\-]+=/)
    private static final PatternFunction attrKeyBoolean = new PatternFunction(~/^\s*[\p{Ll}\p{Lu}0-9_\-]++(?!=)/)
    private static final PatternFunction componentSelfClose = new PatternFunction(~/^\s*\/>/)

    private static final PatternFunction attrValueStringOpen = new PatternFunction(~/^["']/)
    private static final PatternFunction attrValueStringContents = new PatternFunction(~/^(?:[\w\W&&[^\\"]]|\\\\|\\")*(?=")/)
    private static final PatternFunction attrValueStringClose = new PatternFunction(~/["']/)

    private static StackFunctionFsmBuilder<String, State, String> getFsmBuilder() {
        new StackFunctionFsmBuilderImpl<>()
    }

    @Override
    String convert(String src) {
        def b = new StringBuilder()
        def stringAcc = new StringBuilder()

        b << 'def getTemplate() {\nreturn { out ->\n'

        def fsm = getFsmBuilder().with {
            initialState = State.HTML

            whileIn(State.HTML) {
                on html exec {
                    stringAcc << it
                }
                on scriptletOpen shiftTo State.SCRIPTLET exec {
                    if (stringAcc.length() > 0) {
                        b << 'out << """' << stringAcc.toString() << '""";\n'
                        stringAcc = new StringBuilder()
                    }
                }
                on expressionScriptletOpen shiftTo State.EXPRESSION_SCRIPTLET exec {
                    stringAcc << '${'
                }
                on componentOpen shiftTo State.COMPONENT_IDENTIFIER exec {
                    if (stringAcc.length() > 0) {
                        b << 'out << """' << stringAcc.toString() << '""";\n'
                        stringAcc = new StringBuilder()
                    }
                }
            }

            whileIn(State.SCRIPTLET) {
                on scriptletText exec {
                    b << it
                }
                on scriptletClose shiftTo HTML exec {
                    b << ';\n'
                }
            }

            whileIn(State.EXPRESSION_SCRIPTLET) {
                on scriptletText exec {
                    stringAcc << it
                }
                on scriptletClose shiftTo HTML exec {
                    stringAcc << '}'
                }
            }

            whileIn(State.COMPONENT) {
                // tokenize component, figure out body, and tokenize closing component
            }

            whileIn(COMPONENT_IDENTIFIER) {
                on componentIdentifier shiftTo COMPONENT_ATTR_KEY exec {
                    b << "out << renderComponent('${ it }') { attr, bodyOut ->\n"
                }
                onNoMatch() exec {
                    throw new RuntimeException('expected a Component Identifier')
                }
            }

            whileIn(COMPONENT_ATTR_KEY) {
                on attrKeyWithValue shiftTo COMPONENT_ATTR_VALUE_OPEN exec { String s ->
                    def trimmed = s.trim()
                    def key = trimmed.substring(0, trimmed.length() - 1)
                    b << "attr['${ key }'] = "
                }
                on attrKeyBoolean exec { String s ->
                    def trimmed = s.trim()
                    def key = trimmed.substring(0, trimmed.length() - 1)
                    b << "attr['${ key }'] = true"
                }
                on componentSelfClose shiftTo HTML exec {
                    b << '};\n'
                }
                onNoMatch() exec {
                    throw new RuntimeException('expected either an attr key or a closing />')
                }
            }

            whileIn(COMPONENT_ATTR_VALUE_OPEN) {
                on attrValueStringOpen shiftTo COMPONENT_ATTR_VALUE_STRING exec {
                    b << '"'
                }
                onNoMatch() exec {
                    throw new RuntimeException('expected a string opening')
                }
            }

            whileIn(COMPONENT_ATTR_VALUE_STRING) {
                on attrValueStringContents shiftTo COMPONENT_ATTR_VALUE_STRING_CLOSE exec {
                    b << it
                }
                onNoMatch() exec {
                    throw new RuntimeException('expected string contents')
                }
            }

            whileIn(COMPONENT_ATTR_VALUE_STRING_CLOSE) {
                on attrValueStringClose shiftTo COMPONENT_ATTR_KEY exec {
                    b << '";\n'
                }
                onNoMatch() exec {
                    throw new RuntimeException('expected string close')
                }
            }

            build()
        }

        def remaining = src
        while (remaining.length() > 0) {
            def output = fsm.apply(remaining)
            if (output != null) {
                remaining = remaining.substring(output.length())
            } else if (output != null && output.length() == 0) {
                throw new RuntimeException('output length is zero')
            } else {
                throw new RuntimeException('output is null')
            }
        }

        if (fsm.currentState == HTML && stringAcc.length() > 0) {
            b << 'out << """' << stringAcc.toString() << '""";\n'
            stringAcc = new StringBuilder()
        }
        b << '}}\n'

        b.toString()
    }

}
