package com.jessebrault.ssg.specialpage

import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.part.PartRenderer
import com.jessebrault.ssg.part.PartType
import com.jessebrault.ssg.text.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

import static com.jessebrault.ssg.testutil.DiagnosticsUtil.assertEmptyDiagnostics
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.argThat
import static org.mockito.Mockito.when

@ExtendWith(MockitoExtension)
class GspSpecialPageRendererTests {

    private final SpecialPageRenderer renderer = new GspSpecialPageRenderer()

    @Test
    void rendersGlobal() {
        def specialPage = new SpecialPage("<%= globals['greeting'] %>", 'test.gsp', null)
        def globals = [greeting: 'Hello, World!']
        def r = this.renderer.render(specialPage, [], [], globals, '')
        assertEmptyDiagnostics(r)
        assertEquals('Hello, World!', r.v2)
    }

    @Test
    void rendersPartWithNoBinding(@Mock PartRenderer partRenderer) {
        when(partRenderer.render(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new Tuple2<>([], 'Hello, World!'))
        def partType = new PartType([], partRenderer)
        def part = new Part('test', partType , '')

        def specialPage = new SpecialPage("<%= parts['test'].render() %>", 'test.gsp', null)
        def r = this.renderer.render(specialPage, [], [part], [:], '')
        assertEmptyDiagnostics(r)
        assertEquals('Hello, World!', r.v2)
    }

    @Test
    void rendersPartWithBinding(@Mock PartRenderer partRenderer) {
        when(partRenderer.render(
                any(), argThat { Map m -> m.get('greeting') == 'Hello, World!'}, any(), any(), any(), any(), any()
        )).thenReturn(new Tuple2<>([], 'Hello, World!'))
        def partType = new PartType([], partRenderer)
        def part = new Part('test', partType, '')

        def specialPage = new SpecialPage(
                "<%= parts['test'].render([greeting: 'Hello, World!'])",
                'test.gsp',
                null
        )
        def r = this.renderer.render(specialPage, [], [part], [:], '')
        assertEmptyDiagnostics(r)
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
                'test.gsp',
                null
        )
        def r = this.renderer.render(specialPage, [text], [], [:], '')
        assertEmptyDiagnostics(r)
        assertEquals('<p><strong>Hello, World!</strong></p>\n', r.v2)
    }

    @Test
    void tagBuilderAvailable() {
        def specialPage = new SpecialPage('<%= tagBuilder.test() %>', 'test.gsp', null)
        def r = this.renderer.render(specialPage, [], [], [:], '')
        assertEmptyDiagnostics(r)
        assertEquals('<test />', r.v2)
    }

    @Test
    void pathAvailable() {
        def specialPage = new SpecialPage('<%= path %>', 'test.gsp', null)
        def r = this.renderer.render(specialPage, [], [], [:], '')
        assertEmptyDiagnostics(r)
        assertEquals('test.gsp', r.v2)
    }

    @Test
    void urlBuilderAvailable() {
        def specialPage = new SpecialPage(
                '<%= urlBuilder.relative("images/test.jpg") %>',
                'test.gsp',
                null
        )
        def r = this.renderer.render(specialPage, [], [], [:], 'test.html')
        assertEmptyDiagnostics(r)
        assertEquals('images/test.jpg', r.v2)
    }

    @Test
    void urlBuilderIsRelativeToTargetPath() {
        def specialPage = new SpecialPage(
                '<%= urlBuilder.relative("images/test.jpg") %>',
                '',
                null
        )
        def r = this.renderer.render(
                specialPage, [], [], [:], 'test/test.html'
        )
        assertEmptyDiagnostics(r)
        assertEquals('../images/test.jpg', r.v2)
    }

    @Test
    void targetPathAvailable() {
        def specialPage = new SpecialPage(
                '<%= targetPath %>',
                '',
                null
        )
        def r = this.renderer.render(specialPage, [], [], [:], 'test.html')
        assertEmptyDiagnostics(r)
        assertEquals('test.html', r.v2)
    }

}
