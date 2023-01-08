package com.jessebrault.ssg.specialpage

import com.jessebrault.ssg.part.GspPartRenderer
import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.part.PartType
import com.jessebrault.ssg.text.MarkdownFrontMatterGetter
import com.jessebrault.ssg.text.MarkdownTextRenderer
import com.jessebrault.ssg.text.Text
import com.jessebrault.ssg.text.TextType
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class GspSpecialPageRendererTests {

    private final SpecialPageRenderer renderer = new GspSpecialPageRenderer()

    @Test
    void rendersGlobal() {
        def globals = [greeting: 'Hello, World!']
        def r = this.renderer.render("<%= globals['greeting'] %>", [], [], globals)
        assertEquals('Hello, World!', r)
    }

    @Test
    void rendersPartWithNoBinding() {
        def part = new Part('test', new PartType(['.gsp'], new GspPartRenderer()), 'Hello, World!')
        def r = this.renderer.render("<%= parts['test'].render() %>", [], [part], [:])
        assertEquals('Hello, World!', r)
    }

    @Test
    void rendersPartWithBinding() {
        def part = new Part('test', new PartType(['.gsp'], new GspPartRenderer()), "<%= binding['greeting'] %>")
        def r = this.renderer.render("<%= parts['test'].render([greeting: 'Hello, World!'])", [], [part], [:])
        assertEquals('Hello, World!', r)
    }

    @Test
    void rendersText() {
        def text = new Text('**Hello, World!**', 'test', new TextType(['.gsp'], new MarkdownTextRenderer(), new MarkdownFrontMatterGetter()))
        def r = this.renderer.render("<%= texts.find { it.path == 'test' }.render() %>", [text], [], [:])
        assertEquals('<p><strong>Hello, World!</strong></p>\n', r)
    }

}
