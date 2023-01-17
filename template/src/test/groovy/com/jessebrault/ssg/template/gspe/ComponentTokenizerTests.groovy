package com.jessebrault.ssg.template.gspe

import org.junit.jupiter.api.Test

import static com.jessebrault.ssg.template.gspe.ComponentToken.Type.*

import static org.junit.jupiter.api.Assertions.assertEquals

class ComponentTokenizerTests {

    private final ComponentTokenizer tokenizer = new ComponentTokenizer()

    @Test
    void selfClosingComponent() {
        def r = this.tokenizer.tokenize('<Test />')
        assertEquals(4, r.size())
        assertEquals(LT, r[0].type)
        assertEquals(IDENTIFIER, r[1].type)
        assertEquals('Test', r[1].text)
        assertEquals(FORWARD_SLASH, r[2].type)
        assertEquals(GT, r[3].type)
    }

    @Test
    void selfClosingComponentWithDoubleQuotedString() {
        def r = this.tokenizer.tokenize('<Test key="value" />')
        assertEquals(9, r.size())

        assertEquals(LT, r[0].type)

        assertEquals(IDENTIFIER, r[1].type)
        assertEquals('Test', r[1].text)

        assertEquals(KEY, r[2].type)
        assertEquals('key', r[2].text)

        assertEquals(EQUALS, r[3].type)

        assertEquals(DOUBLE_QUOTE, r[4].type)

        assertEquals(STRING, r[5].type)
        assertEquals('value', r[5].text)

        assertEquals(DOUBLE_QUOTE, r[6].type)

        assertEquals(FORWARD_SLASH, r[7].type)

        assertEquals(GT, r[8].type)
    }

    @Test
    void selfClosingComponentWithSingleQuotedString() {
        def r = this.tokenizer.tokenize("<Test key='value' />")
        assertEquals(9, r.size())

        assertEquals(LT, r[0].type)

        assertEquals(IDENTIFIER, r[1].type)
        assertEquals('Test', r[1].text)

        assertEquals(KEY, r[2].type)
        assertEquals('key', r[2].text)

        assertEquals(EQUALS, r[3].type)

        assertEquals(SINGLE_QUOTE, r[4].type)

        assertEquals(STRING, r[5].type)
        assertEquals('value', r[5].text)

        assertEquals(SINGLE_QUOTE, r[6].type)

        assertEquals(FORWARD_SLASH, r[7].type)

        assertEquals(GT, r[8].type)
    }

    @Test
    void componentWithSimpleDollarGroovy() {
        def r = this.tokenizer.tokenize('<Test key=${ test } />')
        assertEquals(10, r.size())

        assertEquals(LT, r[0].type)

        assertEquals(IDENTIFIER, r[1].type)
        assertEquals('Test', r[1].text)

        assertEquals(KEY, r[2].type)
        assertEquals('key', r[2].text)

        assertEquals(EQUALS, r[3].type)

        assertEquals(DOLLAR, r[4].type)

        assertEquals(CURLY_OPEN, r[5].type)

        assertEquals(GROOVY, r[6].type)
        assertEquals(' test ', r[6].text)

        assertEquals(CURLY_CLOSE, r[7].type)

        assertEquals(FORWARD_SLASH, r[8].type)

        assertEquals(GT, r[9].type)
    }

}
