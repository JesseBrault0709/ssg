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

        def r = Tokenizer.tokenize(src)
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
    void doctypeHtml() {
        test('<!DOCTYPE html>') {
            expect LESS_THAN, '<', 1, 1
            expect WORD, '!DOCTYPE', 1, 2
            expect WHITESPACE, ' ', 1, 10
            expect IDENTIFIER, 'html', 1, 11
            expect GREATER_THAN, '>', 1, 15
        }
    }

    @Test
    void htmlLangEn() {
        test('<html lang="en">') {
            expect LESS_THAN, '<', 1, 1
            expect IDENTIFIER, 'html', 1, 2
            expect WHITESPACE, ' ', 1, 6
            expect IDENTIFIER, 'lang', 1, 7
            expect EQUALS, '=', 1, 11
            expect DOUBLE_QUOTE, '"', 1, 12
            expect IDENTIFIER, 'en', 1, 13
            expect DOUBLE_QUOTE, '"', 1, 15
            expect GREATER_THAN, '>', 1, 16
        }
    }

    @Test
    void component() {
        test('<Test />') {
            expect LESS_THAN, '<', 1, 1
            expect CAPITALIZED_IDENTIFIER, 'Test', 1, 2
            expect WHITESPACE, ' ', 1, 6
            expect FORWARD_SLASH, '/', 1, 7
            expect GREATER_THAN, '>', 1, 8
        }
    }

    @Test
    void componentWithKeysAndValues() {
        test('<Test test="test" />') {
            expect LESS_THAN, '<', 1, 1
            expect CAPITALIZED_IDENTIFIER, 'Test', 1, 2
            expect WHITESPACE, ' ', 1, 6
            expect IDENTIFIER, 'test', 1, 7
            expect EQUALS, '=', 1, 11
            expect DOUBLE_QUOTE, '"', 1, 12
            expect STRING, 'test', 1, 13
            expect DOUBLE_QUOTE, '"', 1, 17
            expect WHITESPACE, ' ', 1, 18
            expect FORWARD_SLASH, '/', 1, 19
            expect GREATER_THAN, '>', 1, 20
        }
    }

}
