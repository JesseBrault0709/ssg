package com.jessebrault.ssg.part

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

class GspPartRendererTests {

    private final PartRenderer renderer = new GspPartRenderer()

    @Test
    void rendersWithNoBindingOrGlobals() {
        def part = new Part('', null, 'Hello, World!')
        def r = this.renderer.render(part, [:], [:])
        assertTrue(r.v1.size() == 0)
        assertEquals('Hello, World!', r.v2)
    }

    @Test
    void rendersWithBinding() {
        def part = new Part('', null, "<%= binding['greeting'] %>")
        def r = this.renderer.render(part, [greeting: 'Hello, World!'], [:])
        assertTrue(r.v1.size() == 0)
        assertEquals('Hello, World!', r.v2)
    }

    @Test
    void rendersWithGlobals() {
        def part = new Part(null, null, "<%= globals['greeting'] %>")
        def r = this.renderer.render(part, [:], [greeting: 'Hello, World!'])
        assertTrue(r.v1.size() == 0)
        assertEquals('Hello, World!', r.v2)
    }

}
