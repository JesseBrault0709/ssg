package com.jessebrault.ssg.template

import com.jessebrault.ssg.part.GspPartRenderer
import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.part.PartType
import com.jessebrault.ssg.text.FrontMatter
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class GspTemplateRendererTests {

    private final TemplateRenderer renderer = new GspTemplateRenderer()

    @Test
    void rendersPartWithNoBinding() {
        def template = new Template(
                "<%= parts['test'].render() %>",
                null,
                null
        )
        def part = new Part('test', new PartType(['.gsp'], new GspPartRenderer()), 'Hello, World!')
        def r = this.renderer.render(template, new FrontMatter([:]), '', [part], [:])
        assertEquals('Hello, World!', r)
    }

    @Test
    void rendersPartWithBinding() {
        def template = new Template(
                "<%= parts['greeting'].render([person: 'World']) %>",
                null,
                null
        )
        def part = new Part('greeting', new PartType(['.gsp'], new GspPartRenderer()), 'Hello, $binding.person!')
        def r = this.renderer.render(template, new FrontMatter([:]), '', [part], [:])
        assertEquals('Hello, World!', r)
    }

    @Test
    void rendersFrontMatter() {
        def template = new Template("<%= frontMatter['title'] %>", null, null)
        def r = this.renderer.render(template, new FrontMatter([title: ['Hello!']]), '', [], [:])
        assertEquals('Hello!', r)
    }

    @Test
    void rendersGlobal() {
        def template = new Template("<%= globals['test'] %>", null, null)
        def r = this.renderer.render(template, new FrontMatter([:]), '', [], [test: 'Hello, World!'])
        assertEquals('Hello, World!', r)
    }

    @Test
    void rendersText() {
        def template = new Template('<%= text %>', null, null)
        def r = this.renderer.render(template, new FrontMatter([:]), 'Hello, World!', [], [:])
        assertEquals('Hello, World!', r)
    }

}
