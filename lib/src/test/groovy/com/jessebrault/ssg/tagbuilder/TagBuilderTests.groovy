package com.jessebrault.ssg.tagbuilder

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class TagBuilderTests {

    private final TagBuilder tagBuilder = new DynamicTagBuilder()

    @Test
    void simple() {
        assertEquals('<test />', this.tagBuilder.create('test'))
    }

    @Test
    void withAttributes() {
        assertEquals('<test test="abc" />', this.tagBuilder.create('test', [test: 'abc']))
    }

    @Test
    void withBody() {
        assertEquals('<test>test</test>', this.tagBuilder.create('test', 'test'))
    }

    @Test
    void withAttributesAndBody() {
        assertEquals(
                '<test test="abc">test</test>',
                this.tagBuilder.create('test', [test: 'abc'], 'test')
        )
    }

    @Test
    void dynamicSimple() {
        assertEquals('<test />', this.tagBuilder.test())
    }

    @Test
    void dynamicWithAttributes() {
        assertEquals('<test test="abc" />', this.tagBuilder.test([test: 'abc']))
    }

    @Test
    void dynamicWithBody() {
        assertEquals('<test>test</test>', this.tagBuilder.test('test'))
    }

    @Test
    void dynamicWithAttributesAndBody() {
        assertEquals('<test test="abc">test</test>', this.tagBuilder.test([test: 'abc'], 'test'))
    }

}
