package com.jessebrault.gcp.groovy

import org.junit.jupiter.api.Test

import static com.jessebrault.gcp.groovy.DollarScriptletParser.parse
import static org.junit.jupiter.api.Assertions.assertEquals

class DollarScriptletParserTests {

    @Test
    void empty() {
        assertEquals('${}', parse('${}'))
    }

    @Test
    void simple() {
        assertEquals('${ 1 + 2 }', parse('${ 1 + 2 }'))
    }

    @Test
    void nestedString() {
        assertEquals('${ "myString" }', parse('${ "myString" }'))
    }

    @Test
    void nestedCurlyBraces() {
        assertEquals(
                '${ [1, 2, 3].collect { it + 1 }.size() }',
                parse('${ [1, 2, 3].collect { it + 1 }.size() }')
        )
    }

    @Test
    void nestedSingleQuoteString() {
        assertEquals(
                '${ \'abc\' }',
                parse('${ \'abc\' }')
        )
    }

    @Test
    void nestedGString() {
        assertEquals(
                '${ "abc" }',
                parse('${ "abc" }')
        )
    }

    @Test
    void nestedGStringWithClosure() {
        assertEquals(
                '${ "abc${ it }" }',
                parse('${ "abc${ it }" }')
        )
    }

    @Test
    void takesOnlyAsNeeded() {
        assertEquals(
                '${ 1 + 2 }',
                parse('${ 1 + 2 } someOther=${ 3 + 4 }')
        )
    }

}
