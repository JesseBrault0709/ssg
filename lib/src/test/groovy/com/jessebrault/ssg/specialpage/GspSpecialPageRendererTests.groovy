package com.jessebrault.ssg.specialpage

import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.part.PartRenderer
import com.jessebrault.ssg.part.PartType
import com.jessebrault.ssg.text.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue
import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.argThat
import static org.mockito.Mockito.when

@ExtendWith(MockitoExtension)
class GspSpecialPageRendererTests {

    private final SpecialPageRenderer renderer = new GspSpecialPageRenderer()

    @Test
    void rendersGlobal() {
        def specialPage = new SpecialPage("<%= globals['greeting'] %>", null, null)
        def globals = [greeting: 'Hello, World!']
        def r = this.renderer.render(specialPage, [], [], globals)
        assertTrue(r.v1.size() == 0)
        assertEquals('Hello, World!', r.v2)
    }

    @Test
    void rendersPartWithNoBinding(@Mock PartRenderer partRenderer) {
        when(partRenderer.render(any(), any(), any(), any())).thenReturn(new Tuple2<>([], 'Hello, World!'))
        def partType = new PartType([], partRenderer)
        def part = new Part('test', partType , '')

        def specialPage = new SpecialPage("<%= parts['test'].render() %>", null, null)
        def r = this.renderer.render(specialPage, [], [part], [:])
        assertTrue(r.v1.size() == 0)
        assertEquals('Hello, World!', r.v2)
    }

    @Test
    void rendersPartWithBinding(@Mock PartRenderer partRenderer) {
        when(partRenderer.render(any(), argThat { Map m -> m.get('greeting') == 'Hello, World!'}, any(), any()))
                .thenReturn(new Tuple2<>([], 'Hello, World!'))
        def partType = new PartType([], partRenderer)
        def part = new Part('test', partType, '')

        def specialPage = new SpecialPage(
                "<%= parts['test'].render([greeting: 'Hello, World!'])",
                null,
                null
        )
        def r = this.renderer.render(specialPage, [], [part], [:])
        assertTrue(r.v1.size() == 0)
        assertEquals('Hello, World!', r.v2)
    }

    @Test
    void rendersText(@Mock TextRenderer textRenderer, @Mock FrontMatterGetter frontMatterGetter) {
        when(textRenderer.render(any(), any()))
                .thenReturn(new Tuple2<>([], '<p><strong>Hello, World!</strong></p>\n'))
        def textType = new TextType([], textRenderer, frontMatterGetter, new MarkdownExcerptGetter())
        def text = new Text('', 'test', textType)

        def specialPage = new SpecialPage(
                "<%= texts.find { it.path == 'test' }.render() %>",
                null,
                null
        )
        def r = this.renderer.render(specialPage, [text], [], [:])
        assertTrue(r.v1.size() == 0)
        assertEquals('<p><strong>Hello, World!</strong></p>\n', r.v2)
    }

}
