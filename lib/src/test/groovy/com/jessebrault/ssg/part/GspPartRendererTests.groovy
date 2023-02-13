package com.jessebrault.ssg.part

import com.jessebrault.ssg.Diagnostic
import com.jessebrault.ssg.text.*
import org.junit.jupiter.api.Test

import static com.jessebrault.ssg.testutil.DiagnosticsUtil.getDiagnosticsMessageSupplier
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
        def r = this.renderer.render(part, [:], [:], null, [part])
        assertTrue(r.v1.size() == 0)
        assertEquals('Hello, World!', r.v2)
    }

    @Test
    void rendersWithBinding() {
        def part = new Part('', null, "<%= binding['greeting'] %>")
        def r = this.renderer.render(
                part,
                [greeting: 'Hello, World!'],
                [:],
                null,
                [part]
        )
        assertTrue(r.v1.size() == 0)
        assertEquals('Hello, World!', r.v2)
    }

    @Test
    void rendersWithGlobals() {
        def part = new Part(null, null, "<%= globals['greeting'] %>")
        def r = this.renderer.render(
                part,
                [:],
                [greeting: 'Hello, World!'],
                null,
                [part]
        )
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
                ),
                [part]
        )
        assertTrue(textDiagnostics.isEmpty())
        assertTrue(r.v1.isEmpty())
        assertEquals('Hello, World!', r.v2)
    }

    @Test
    void tagBuilderAvailable() {
        def part = new Part('', null, '<%= tagBuilder.test() %>')
        def r = this.renderer.render(part, [:], [:], null, [part])
        assertTrue(r.v1.isEmpty())
        assertEquals('<test />', r.v2)
    }

    @Test
    void allPartsAvailable() {
        def partType = new PartType(['.gsp'], this.renderer)
        def part0 = new Part('part0.gsp', partType, '<%= parts["part1.gsp"].render() %>')
        def part1 = new Part('part1.gsp', partType, 'Hello, World!')
        def r = this.renderer.render(part0, [:], [:], null, [part0, part1])
        assertTrue(r.v1.isEmpty(), getDiagnosticsMessageSupplier(r.v1))
        assertEquals('Hello, World!', r.v2)
    }

}
