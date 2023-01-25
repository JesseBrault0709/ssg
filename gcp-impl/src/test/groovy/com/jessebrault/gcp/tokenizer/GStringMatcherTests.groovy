package com.jessebrault.gcp.tokenizer

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class GStringMatcherTests {

    private final GStringMatcher matcher = new GStringMatcher()

    private void test(String expectedEntire, String input) {
        def output = this.matcher.apply(input)
        assertEquals(expectedEntire, output.entire())
        assertEquals('"', output.part(1))
        assertEquals(expectedEntire.substring(1, expectedEntire.length() - 1), output.part(2))
        assertEquals('"', output.part(3))
    }

    @Test
    void empty() {
        test '""', '""'
    }

    @Test
    void simple() {
        test '"abc"', '"abc"'
    }

    @Test
    void nestedDollarClosureWithGString() {
        test '"abc ${ \'def\'.each { "$it " }.join() }"', '"abc ${ \'def\'.each { "$it " }.join() }"'
    }

    @Test
    void nestedDollarClosureWithGStringTakesOnlyAsNeeded() {
        test '"abc ${ \'def\'.each { "$it " }.join() }"', '"abc ${ \'def\'.each { "$it " }.join() }" test="rest"'
    }

    @Test
    void takesOnlyAsNeeded() {
        test '"abc"', '"abc" test="def"'
    }

}
