package com.jessebrault.ssg.template

import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.part.PartRenderer
import com.jessebrault.ssg.part.PartType
import com.jessebrault.ssg.text.ExcerptGetter
import com.jessebrault.ssg.text.FrontMatter
import com.jessebrault.ssg.text.FrontMatterGetter
import com.jessebrault.ssg.text.Text
import com.jessebrault.ssg.text.TextRenderer
import com.jessebrault.ssg.text.TextType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue
import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.argThat
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

@ExtendWith(MockitoExtension)
class GspTemplateRendererTests {

    /**
     * TODO: move to a fixture
     */
    private static Text mockBlankText() {
        def textRenderer = mock(TextRenderer)
        def frontMatterGetter = mock(FrontMatterGetter)
        def excerptGetter = mock(ExcerptGetter)
        new Text('', '', new TextType([], textRenderer, frontMatterGetter, excerptGetter))
    }

    /**
     * TODO: move to a fixture
     */
    private static Text mockRenderableText(String text) {
        def textRenderer = mock(TextRenderer)
        when(textRenderer.render(any(), any())).thenReturn(new Tuple2<>([], text))
        def frontMatterGetter = mock(FrontMatterGetter)
        def excerptGetter = mock(ExcerptGetter)
        new Text('', '', new TextType([], textRenderer, frontMatterGetter, excerptGetter))
    }

    private final TemplateRenderer renderer = new GspTemplateRenderer()

    @Test
    void rendersPartWithNoBinding(@Mock PartRenderer partRenderer) {
        def template = new Template(
                "<%= parts['test'].render() %>",
                null,
                null
        )

        when(partRenderer.render(any(), any(), any(), any())).thenReturn(new Tuple2<>([], 'Hello, World!'))
        def partType = new PartType([], partRenderer)
        def part = new Part('test', partType, null)

        def r = this.renderer.render(
                template,
                new FrontMatter(null, [:]),
                mockBlankText(),
                [part],
                [:]
        )
        assertTrue(r.v1.size() == 0)
        assertEquals('Hello, World!', r.v2)
    }

    @Test
    void rendersPartWithBinding(@Mock PartRenderer partRenderer) {
        def template = new Template(
                "<%= parts['greeting'].render([person: 'World']) %>",
                null,
                null
        )

        when(partRenderer.render(any(), argThat { Map m -> m.get('person') == 'World' }, any(), any()))
                .thenReturn(new Tuple2<>([], 'Hello, World!'))
        def partType = new PartType([], partRenderer)
        def part = new Part('greeting', partType, null)

        def r = this.renderer.render(
                template,
                new FrontMatter(null, [:]),
                mockBlankText(),
                [part],
                [:]
        )
        assertTrue(r.v1.size() == 0)
        assertEquals('Hello, World!', r.v2)
    }

    @Test
    void rendersFrontMatter() {
        def template = new Template("<%= frontMatter['title'] %>", null, null)
        def r = this.renderer.render(
                template,
                new FrontMatter(null, [title: ['Hello!']]),
                mockBlankText(),
                [],
                [:]
        )
        assertTrue(r.v1.size() == 0)
        assertEquals('Hello!', r.v2)
    }

    @Test
    void rendersGlobal() {
        def template = new Template("<%= globals['test'] %>", null, null)
        def r = this.renderer.render(
                template,
                new FrontMatter(null, [:]),
                mockBlankText(),
                [],
                [test: 'Hello, World!']
        )
        assertTrue(r.v1.size() == 0)
        assertEquals('Hello, World!', r.v2)
    }

    @Test
    void textAvailableToRender() {
        def template = new Template('<%= text.render() %>', null, null)
        def r = this.renderer.render(
                template,
                new FrontMatter(null, [:]),
                mockRenderableText('Hello, World!'),
                [],
                [:]
        )
        assertTrue(r.v1.size() == 0)
        assertEquals('Hello, World!', r.v2)
    }

}
