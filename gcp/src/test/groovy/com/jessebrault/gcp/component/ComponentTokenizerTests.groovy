package com.jessebrault.gcp.component


import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static com.jessebrault.gcp.component.ComponentToken.Type.*
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

class ComponentTokenizerTests {

    private static final Logger logger = LoggerFactory.getLogger(ComponentTokenizerTests)

    private static class TokenSpec {

        ComponentToken.Type type
        String text

        TokenSpec(ComponentToken.Type type, String text = null) {
            this.type = Objects.requireNonNull(type)
            this.text = text
        }

        void compare(ComponentToken actual) {
            assertEquals(this.type, actual.type)
            if (this.text != null) {
                assertEquals(this.text, actual.text)
            }
        }

        @Override
        String toString() {
            "TokenSpec(${ this.type }, ${ this.text })"
        }

    }

    private static class TesterConfigurator {

        Queue<TokenSpec> specs = new LinkedList<>()

        void expect(ComponentToken.Type type, String text) {
            this.specs << new TokenSpec(type, text)
        }

        void expect(ComponentToken.Type type) {
            this.specs << new TokenSpec(type)
        }

    }

    private final ComponentTokenizer tokenizer = new ComponentTokenizer()

    private void test(
            String src,
            @DelegatesTo(value = TesterConfigurator, strategy = Closure.DELEGATE_FIRST)
            Closure<Void> configure
    ) {
        def configurator = new TesterConfigurator()
        configure.setDelegate(configurator)
        configure.setResolveStrategy(Closure.DELEGATE_FIRST)
        configure()

        def r = this.tokenizer.tokenize(src)
        logger.debug('r: {}', r)
        logger.debug('configurator.specs: {}', configurator.specs)

        assertEquals(configurator.specs.size(), r.size())

        def resultIterator = r.iterator()
        configurator.specs.each {
            assertTrue(resultIterator.hasNext())
            it.compare(resultIterator.next())
        }
    }

    @Test
    void selfClosingComponent() {
        this.test('<Test />') {
            expect LT
            expect IDENTIFIER, 'Test'
            expect FORWARD_SLASH
            expect GT
        }
    }

    @Test
    void selfClosingComponentWithDoubleQuotedString() {
        this.test('<Test key="value" />') {
            expect LT
            expect IDENTIFIER, 'Test'
            expect KEY, 'key'
            expect EQUALS
            expect DOUBLE_QUOTE
            expect STRING, 'value'
            expect DOUBLE_QUOTE
            expect FORWARD_SLASH
            expect GT
        }
    }

    @Test
    void selfClosingComponentWithSingleQuotedString() {
        this.test("<Test key='value' />") {
            expect LT
            expect IDENTIFIER, 'Test'
            expect KEY, 'key'
            expect EQUALS
            expect SINGLE_QUOTE
            expect STRING, 'value'
            expect SINGLE_QUOTE
            expect FORWARD_SLASH
            expect GT
        }
    }

    @Test
    void componentWithSimpleDollarGroovy() {
        this.test('<Test key=${ test } />') {
            expect LT
            expect IDENTIFIER, 'Test'
            expect KEY, 'key'
            expect EQUALS
            expect GROOVY, ' test '
            expect FORWARD_SLASH
            expect GT
        }
    }

    @Test
    void dollarGroovyNestedBraces() {
        this.test('<Test key=${ test.each { it.test() } } />') {
            expect LT
            expect IDENTIFIER, 'Test'
            expect KEY, 'key'
            expect EQUALS
            expect GROOVY, ' test.each { it.test() } '
            expect FORWARD_SLASH
            expect GT
        }
    }

    @Test
    void dollarReference() {
        this.test('<Test key=$test />') {
            expect LT
            expect IDENTIFIER, 'Test'
            expect KEY, 'key'
            expect EQUALS
            expect GROOVY_IDENTIFIER, 'test'
            expect FORWARD_SLASH
            expect GT
        }
    }

}
