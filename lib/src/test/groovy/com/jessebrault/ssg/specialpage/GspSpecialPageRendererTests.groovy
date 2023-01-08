package com.jessebrault.ssg.specialpage

import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.part.PartRenderer
import com.jessebrault.ssg.part.PartType
import com.jessebrault.ssg.text.FrontMatterGetter
import com.jessebrault.ssg.text.Text
import com.jessebrault.ssg.text.TextRenderer
import com.jessebrault.ssg.text.TextType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.argThat
import static org.mockito.Mockito.when

@ExtendWith(MockitoExtension)
class GspSpecialPageRendererTests {

    private final SpecialPageRenderer renderer = new GspSpecialPageRenderer()

    @Test
    void rendersGlobal() {
        def globals = [greeting: 'Hello, World!']
        def r = this.renderer.render("<%= globals['greeting'] %>", [], [], globals)
        assertEquals('Hello, World!', r)
    }

    @Test
    void rendersPartWithNoBinding(@Mock PartRenderer partRenderer) {
        when(partRenderer.render(any(), any(), any())).thenReturn('Hello, World!')
        def partType = new PartType([], partRenderer)
        def part = new Part('test', partType , '')

        def r = this.renderer.render("<%= parts['test'].render() %>", [], [part], [:])
        assertEquals('Hello, World!', r)
    }

    @Test
    void rendersPartWithBinding(@Mock PartRenderer partRenderer) {
        when(partRenderer.render(any(), argThat { Map m -> m.get('greeting') == 'Hello, World!'}, any())).thenReturn('Hello, World!')
        def partType = new PartType([], partRenderer)
        def part = new Part('test', partType, '')

        def r = this.renderer.render("<%= parts['test'].render([greeting: 'Hello, World!'])", [], [part], [:])
        assertEquals('Hello, World!', r)
    }

    @Test
    void rendersText(@Mock TextRenderer textRenderer, @Mock FrontMatterGetter frontMatterGetter) {
        when(textRenderer.render(any(), any())).thenReturn('<p><strong>Hello, World!</strong></p>\n')
        def textType = new TextType([], textRenderer, frontMatterGetter)
        def text = new Text('', 'test', textType)

        def r = this.renderer.render("<%= texts.find { it.path == 'test' }.render() %>", [text], [], [:])
        assertEquals('<p><strong>Hello, World!</strong></p>\n', r)
    }

}
