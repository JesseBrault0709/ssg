package com.jessebrault.gcp.tokenizer

import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static com.jessebrault.gcp.tokenizer.Token.Type.*
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

class TokenizerTests {

    private static final Logger logger = LoggerFactory.getLogger(TokenizerTests)

    private static class TokenSpec {

        Token.Type type
        String text
        int line
        int col

        TokenSpec(Token.Type type, String text = null, line = 0, col = 0) {
            this.type = Objects.requireNonNull(type)
            this.text = text
            this.line = line
            this.col = col
        }

        void compare(Token actual) {
            assertEquals(this.type, actual.type)
            if (this.text != null) {
                assertEquals(this.text, actual.text)
            }
            if (this.line != 0) {
                assertEquals(this.line, actual.line)
            }
            if (this.col != 0) {
                assertEquals(this.col, actual.col)
            }
        }

        @Override
        String toString() {
            "TokenSpec(${ this.type }, ${ this.text }, ${ this.line }, ${ this.col })"
        }

    }

    private static class TesterConfigurator {

        Queue<TokenSpec> specs = new LinkedList<>()

        void expect(Token.Type type, String text = null, line = 0, col = 0) {
            this.specs << new TokenSpec(type, text, line, col)
        }

    }

    private static void test(
            String src,
            @DelegatesTo(value = TesterConfigurator, strategy = Closure.DELEGATE_FIRST)
            Closure<Void> configure
    ) {
        def configurator = new TesterConfigurator()
        configure.setDelegate(configurator)
        configure.setResolveStrategy(Closure.DELEGATE_FIRST)
        configure()

        def r = new TokenizerImpl().tokenizeAll(src, Tokenizer.State.TEXT)
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
    void doctypeHtmlIsText() {
        test('<!DOCTYPE html>') {
            expect TEXT, '<!DOCTYPE html>', 1, 1
        }
    }

    @Test
    void htmlLangEnIsText() {
        test('<html lang="en">') {
            expect TEXT, '<html lang="en">', 1, 1
        }
    }

    @Test
    void component() {
        test('<Test />') {
            expect COMPONENT_START, '<', 1, 1
            expect CLASS_NAME, 'Test', 1, 2
            expect WHITESPACE, ' ', 1, 6
            expect FORWARD_SLASH, '/', 1, 7
            expect COMPONENT_END, '>', 1, 8
        }
    }

    @Test
    void componentWithGString() {
        test('<Test test="test" />') {
            expect COMPONENT_START, '<', 1, 1
            expect CLASS_NAME, 'Test', 1, 2
            expect WHITESPACE, ' ', 1, 6
            expect KEY, 'test', 1, 7
            expect EQUALS, '=', 1, 11
            expect DOUBLE_QUOTE, '"', 1, 12
            expect STRING, 'test', 1, 13
            expect DOUBLE_QUOTE, '"', 1, 17
            expect WHITESPACE, ' ', 1, 18
            expect FORWARD_SLASH, '/', 1, 19
            expect COMPONENT_END, '>', 1, 20
        }
    }

    @Test
    void componentWithGStringWithNestedGString() {
        test('<Test test="abc ${ \'abc\'.collect { "it " }.join() }" />') {
            expect COMPONENT_START, '<', 1, 1
            expect CLASS_NAME, 'Test', 1, 2
            expect WHITESPACE, ' ', 1, 6
            expect KEY, 'test', 1, 7
            expect EQUALS, '=', 1, 11
            expect DOUBLE_QUOTE, '"', 1, 12
            expect STRING, 'abc ${ \'abc\'.collect { "it " }.join() }', 1, 13
            expect DOUBLE_QUOTE, '"', 1, 52
            expect WHITESPACE, ' ', 1, 53
            expect FORWARD_SLASH, '/', 1, 54
            expect COMPONENT_END, '>', 1, 55
        }
    }

    @Test
    void newlinesCounted() {
        test('Hello,\n$person!') {
            expect TEXT, 'Hello,\n', 1, 1
            expect DOLLAR, '$', 2, 1
            expect GROOVY_REFERENCE, 'person', 2, 2
            expect TEXT, '!', 2, 8
        }
    }

    @Test
    void componentWithSingleQuoteString() {
        test("<Test test='Hello, World!' />") {
            expect COMPONENT_START, '<', 1, 1
            expect CLASS_NAME, 'Test', 1, 2
            expect WHITESPACE, ' ', 1, 6
            expect KEY, 'test', 1, 7
            expect EQUALS, '=', 1, 11
            expect SINGLE_QUOTE, "'", 1, 12
            expect STRING, 'Hello, World!', 1, 13
            expect SINGLE_QUOTE, "'", 1, 26
            expect WHITESPACE, ' ', 1, 27
            expect FORWARD_SLASH, '/', 1, 28
            expect COMPONENT_END, '>', 1, 29
        }
    }

    @Test
    void componentWithFullyQualifiedName() {
        test('<com.jessebrault.gcp.Test />') {
            expect COMPONENT_START, '<', 1, 1
            expect PACKAGE_NAME, 'com', 1, 2
            expect DOT, '.', 1, 5
            expect PACKAGE_NAME, 'jessebrault', 1, 6
            expect DOT, '.', 1, 17
            expect PACKAGE_NAME, 'gcp', 1, 18
            expect DOT, '.', 1, 21
            expect CLASS_NAME, 'Test', 1, 22
            expect WHITESPACE, ' ', 1, 26
            expect FORWARD_SLASH, '/', 1, 27
            expect COMPONENT_END, '>', 1, 28
        }
    }

    @Test
    void componentWithNewlineWhitespace() {
        test('<Test\n/>') {
            expect COMPONENT_START, '<', 1, 1
            expect CLASS_NAME, 'Test', 1, 2
            expect WHITESPACE, '\n', 1, 6
            expect FORWARD_SLASH, '/', 2, 1
            expect COMPONENT_END, '>', 2, 2
        }
    }

}
