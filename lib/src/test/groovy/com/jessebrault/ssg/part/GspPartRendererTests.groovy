package com.jessebrault.ssg.part

import com.jessebrault.ssg.Diagnostic
import com.jessebrault.ssg.text.*
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue
import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class GspPartRendererTests {

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

    private final PartRenderer renderer = new GspPartRenderer()

    @Test
    void rendersWithNoBindingOrGlobals() {
        def part = new Part('', null, 'Hello, World!')
        def r = this.renderer.render(part, [:], [:], null)
        assertTrue(r.v1.size() == 0)
        assertEquals('Hello, World!', r.v2)
    }

    @Test
    void rendersWithBinding() {
        def part = new Part('', null, "<%= binding['greeting'] %>")
        def r = this.renderer.render(part, [greeting: 'Hello, World!'], [:], null)
        assertTrue(r.v1.size() == 0)
        assertEquals('Hello, World!', r.v2)
    }

    @Test
    void rendersWithGlobals() {
        def part = new Part(null, null, "<%= globals['greeting'] %>")
        def r = this.renderer.render(part, [:], [greeting: 'Hello, World!'], null)
        assertTrue(r.v1.size() == 0)
        assertEquals('Hello, World!', r.v2)
    }

    @Test
    void textAvailable() {
        def part = new Part('', null, '<%= text.render() %>')
        def textDiagnostics = []
        def r = this.renderer.render(
                part,
                [:],
                [:],
                new EmbeddableText(
                        mockRenderableText('Hello, World!'),
                        [:],
                        { Collection<Diagnostic> diagnostics -> textDiagnostics.addAll(diagnostics) }
                )
        )
        assertTrue(textDiagnostics.isEmpty())
        assertTrue(r.v1.isEmpty())
        assertEquals('Hello, World!', r.v2)
    }

    @Test
    void tagBuilderAvailable() {
        def part = new Part('', null, '<%= tagBuilder.test() %>')
        def r = this.renderer.render(part, [:], [:], null)
        assertTrue(r.v1.isEmpty())
        assertEquals('<test />', r.v2)
    }

}
