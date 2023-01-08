package com.jessebrault.ssg.part

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class GspPartRendererTests {

    private final PartRenderer renderer = new GspPartRenderer()

    @Test
    void rendersWithNoBindingOrGlobals() {
        def r = this.renderer.render('Hello, World!', [:], [:])
        assertEquals('Hello, World!', r)
    }

    @Test
    void rendersWithBinding() {
        def r = this.renderer.render("<%= binding['greeting'] %>", [greeting: 'Hello, World!'], [:])
        assertEquals('Hello, World!', r)
    }

    @Test
    void rendersWithGlobals() {
        def r = this.renderer.render("<%= globals['greeting'] %>", [:], [greeting: 'Hello, World!'])
        assertEquals('Hello, World!', r)
    }

}
