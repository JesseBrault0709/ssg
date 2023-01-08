package com.jessebrault.ssg.template

import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.part.PartRenderer
import com.jessebrault.ssg.part.PartType
import com.jessebrault.ssg.text.FrontMatter
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.argThat
import static org.mockito.Mockito.when

@ExtendWith(MockitoExtension)
class GspTemplateRendererTests {

    private final TemplateRenderer renderer = new GspTemplateRenderer()

    @Test
    void rendersPartWithNoBinding(@Mock PartRenderer partRenderer) {
        def template = new Template(
                "<%= parts['test'].render() %>",
                null,
                null
        )

        when(partRenderer.render(any(), any(), any())).thenReturn('Hello, World!')
        def partType = new PartType([], partRenderer)
        def part = new Part('test', partType, null)

        def r = this.renderer.render(template, new FrontMatter([:]), '', [part], [:])
        assertEquals('Hello, World!', r)
    }

    @Test
    void rendersPartWithBinding(@Mock PartRenderer partRenderer) {
        def template = new Template(
                "<%= parts['greeting'].render([person: 'World']) %>",
                null,
                null
        )

        when(partRenderer.render(any(), argThat { Map m -> m.get('person') == 'World' }, any())).thenReturn('Hello, World!')
        def partType = new PartType([], partRenderer)
        def part = new Part('greeting', partType, null)

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
