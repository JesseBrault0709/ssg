package com.jessebrault.gcp.tokenizer

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class DollarScriptletMatcherTests {

    private final DollarScriptletMatcher matcher = new DollarScriptletMatcher();

    private void test(String expectedEntire, String input) {
        def r = this.matcher.apply(input)
        assertEquals(expectedEntire, r.entire())
        assertEquals('$', r.part(1))
        assertEquals('{', r.part(2))
        assertEquals(expectedEntire.substring(2, expectedEntire.length() - 1), r.part(3))
        assertEquals('}', r.part(4))
    }

    @Test
    void empty() {
        test '${}', '${}'
    }

    @Test
    void simple() {
        test '${ 1 + 2 }', '${ 1 + 2 }'
    }

    @Test
    void nestedString() {
        test '${ "myString" }', '${ "myString" }'
    }

    @Test
    void nestedCurlyBraces() {
        test '${ [1, 2, 3].collect { it + 1 }.size() }', '${ [1, 2, 3].collect { it + 1 }.size() }'
    }

    @Test
    void nestedSingleQuoteString() {
        test '${ \'abc\' }', '${ \'abc\' }'
    }

    @Test
    void nestedGString() {
        test '${ "abc" }', '${ "abc" }'
    }

    @Test
    void nestedGStringWithClosure() {
        test '${ "abc${ it }" }', '${ "abc${ it }" }'
    }

    @Test
    void takesOnlyAsNeeded() {
        test '${ 1 + 2 }', '${ 1 + 2 } someOther=${ 3 + 4 }'
    }

}
