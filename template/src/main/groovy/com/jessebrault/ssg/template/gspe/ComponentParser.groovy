package com.jessebrault.ssg.template.gspe


import groovy.transform.PackageScope

import static com.jessebrault.ssg.template.gspe.ComponentToken.Type.*

/**
 * NOT thread safe
 */
@PackageScope
class ComponentParser {

    private Queue<ComponentToken> tokens
    private StringBuilder b

    private String identifier

    String parse(List<ComponentToken> tokens) {
        this.b = new StringBuilder()
        this.tokens = new LinkedList<>(tokens)

        this.selfClosingComponent()

        this.b.toString()
    }

    String parse(List<ComponentToken> openingTokens, String bodyClosure, List<ComponentToken> closingTokens) {
        this.b = new StringBuilder()

        this.tokens = new LinkedList<>(openingTokens)
        this.openingComponent()

        this.b << "bodyOut << ${ bodyClosure };"

        this.tokens = new LinkedList<>(closingTokens)
        this.closingComponent()

        this.b.toString()
    }

    private static void error(Collection<ComponentToken.Type> expectedTypes, ComponentToken actual) {
        throw new RuntimeException("expected ${ expectedTypes.join(' or ') } but got ${ actual ? "'${ actual }'" : 'null' }")
    }

    private void selfClosingComponent() {
        this.startOfOpeningOrSelfClosingComponent()
        this.keysAndValues()
        def t0 = this.tokens.poll()
        if (!t0 || t0.type != FORWARD_SLASH) {
            error([FORWARD_SLASH], t0)
        } else {
            def t1 = tokens.poll()
            if (!t1 || t1.type != GT) {
                error([GT], t1)
            } else {
                this.b << '};'
            }
        }
    }

    private void openingComponent() {
        this.startOfOpeningOrSelfClosingComponent()
        this.keysAndValues()
        def t0 = tokens.poll()
        if (!t0 || t0.type != GT) {
            error([GT], t0)
        }
    }

    private void closingComponent() {
        def t0 = this.tokens.poll()
        if (!t0 || t0.type != LT) {
            error([LT], t0)
        } else {
            def t1 = this.tokens.poll()
            if (!t1 || t1.type != FORWARD_SLASH) {
                error([FORWARD_SLASH], t1)
            } else {
                def t2 = this.tokens.poll()
                if (!t2 || t2.type != IDENTIFIER) {
                    error([IDENTIFIER], t2)
                } else if (t2.text != identifier) {
                    throw new RuntimeException("expected '${ this.identifier }' but got '${ t2.text }'")
                } else {
                    def t3 = this.tokens.poll()
                    if (!t3 || t3.type != GT) {
                        error([GT], t3)
                    } else {
                        this.b << '};'
                    }
                }
            }
        }
    }

    private void startOfOpeningOrSelfClosingComponent() {
        def t0 = this.tokens.poll()
        if (!t0 || t0.type != LT) {
            error([LT], t0)
        } else {
            def t1 = this.tokens.poll()
            if (!t1 || t1.type != IDENTIFIER) {
                error([IDENTIFIER], t1)
            } else {
                this.identifier = t1.text
                this.b << "renderComponent('${ this.identifier }') { attr, bodyOut ->\n"
            }
        }
    }

    private void keysAndValues() {
        while (true) {
            def t0 = this.tokens.peek()
            if (!t0 || !t0.type.isAnyOf([KEY, FORWARD_SLASH])) {
                error([KEY, FORWARD_SLASH], t0)
            } else if (t0.type == KEY) {
                this.keyAndValue()
            } else if (t0.type == FORWARD_SLASH) {
                break
            }
        }
    }

    @PeekBefore(KEY)
    private void keyAndValue() {
        def t0 = this.tokens.remove()
        if (t0.type != KEY) {
            throw new RuntimeException('programmer error')
        } else {
            def t1 = this.tokens.poll()
            if (!t1 || t1.type != EQUALS) {
                error([EQUALS], t1)
            } else {
                this.b << "attr['${ t0.text }'] = "
                this.value()
            }
        }
    }

    private void value() {
        def t0 = this.tokens.peek()
        if (!t0 || !t0.type.isAnyOf([DOUBLE_QUOTE, SINGLE_QUOTE, DOLLAR, LT])) {
            error([DOUBLE_QUOTE, SINGLE_QUOTE, DOLLAR, LT], t0)
        } else if (t0.type == DOUBLE_QUOTE) {
            this.doubleQuoteStringValue()
        } else if (t0.type == SINGLE_QUOTE) {
            this.singleQuoteStringValue()
        } else if (t0.type == DOLLAR) {
            this.dollarValue()
        } else if (t0.type == LT) {
            this.scriptletValue()
        }
    }

    @PeekBefore(DOUBLE_QUOTE)
    private void doubleQuoteStringValue() {
        def t0 = this.tokens.remove()
        if (t0.type != DOUBLE_QUOTE) {
            throw new RuntimeException('programmer error')
        } else {
            def t1 = this.tokens.poll()
            if (!t1 || t1.type != STRING) {
                error([STRING], t1)
            } else {
                def t2 = this.tokens.poll()
                if (!t2 || t2.type != DOUBLE_QUOTE) {
                    error([DOUBLE_QUOTE], t2)
                } else {
                    this.b << /"${ t1.text }";/ + '\n'
                }
            }
        }
    }

    @PeekBefore(SINGLE_QUOTE)
    private void singleQuoteStringValue() {
        def t0 = this.tokens.remove()
        if (t0.type != SINGLE_QUOTE) {
            throw new RuntimeException('programmer error')
        } else {
            def t1 = tokens.poll()
            if (!t1 || t1.type != STRING) {
                error([STRING], t1)
            } else {
                def t2 = this.tokens.poll()
                if (!t2 || t2.type != SINGLE_QUOTE) {
                    error([SINGLE_QUOTE], t2)
                } else {
                    this.b << /'${ t1.text }';/ + '\n'
                }
            }
        }
    }

    @PeekBefore(DOLLAR)
    private void dollarValue() {
        def t0 = this.tokens.remove()
        if (t0.type != DOLLAR) {
            throw new RuntimeException('programmer error')
        } else {
            def t1 = this.tokens.poll()
            if (!t1 || t1.type != CURLY_OPEN) {
                error([CURLY_OPEN], t1)
            } else {
                def t2 = this.tokens.poll()
                if (!t2 || t2.type != GROOVY) {
                    error([GROOVY], t2)
                } else {
                    def t3 = this.tokens.poll()
                    if (!t3 || t3.type != CURLY_CLOSE) {
                        error([CURLY_CLOSE], t3)
                    } else {
                        this.b << "${ t2.text };\n"
                    }
                }
            }
        }
    }

    @PeekBefore(LT)
    private void scriptletValue() {
        def t0 = this.tokens.remove()
        if (t0.type != LT) {
            throw new RuntimeException('programmer error')
        } else {
            def t1 = this.tokens.poll()
            if (!t1 || t1.type != PERCENT) {
                error([PERCENT], t1)
            } else {
                def t2 = this.tokens.poll()
                if (!t2 || t2.type != EQUALS) {
                    error([EQUALS], t2)
                } else {
                    def t3 = this.tokens.poll()
                    if (!t3 || t3.type != GROOVY) {
                        error([GROOVY], t3)
                    } else {
                        def t4 = this.tokens.poll()
                        if (!t4.type || t4.type != PERCENT) {
                            error([PERCENT], t4)
                        } else {
                            def t5 = this.tokens.poll()
                            if (!t5 || t5.type != GT) {
                                error([GT], t5)
                            } else {
                                this.b << "${ t3.text };\n"
                            }
                        }
                    }
                }
            }
        }
    }

}
