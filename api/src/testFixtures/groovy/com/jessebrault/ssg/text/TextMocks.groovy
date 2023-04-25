package com.jessebrault.ssg.text

import com.jessebrault.ssg.util.Result

import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

final class TextMocks {

    static Text blankText() {
        def textRenderer = mock(TextRenderer)
        def frontMatterGetter = mock(FrontMatterGetter)
        def excerptGetter = mock(ExcerptGetter)
        new Text('', new TextType([], textRenderer, frontMatterGetter, excerptGetter), '')
    }

    static Text nonRenderableText(String text) {
        def textRenderer = mock(TextRenderer)
        when(textRenderer.render(any())).thenReturn(Result.of(text))
        new Text('', new TextType([], textRenderer, mock(FrontMatterGetter), mock(ExcerptGetter)), text)
    }

    static Text renderableText(String text) {
        def textRenderer = mock(TextRenderer)
        when(textRenderer.render(any())).thenReturn(Result.of(text))
        def frontMatterGetter = mock(FrontMatterGetter)
        def excerptGetter = mock(ExcerptGetter)
        new Text('', new TextType([], textRenderer, frontMatterGetter, excerptGetter), text)
    }

    static Text textWithPath(String path) {
        def textRenderer = mock(TextRenderer)
        def frontMatterGetter = mock(FrontMatterGetter)
        def excerptGetter = mock(ExcerptGetter)
        new Text(path, new TextType([], textRenderer, frontMatterGetter, excerptGetter), '')
    }

    static Text renderableTextWithPath(String text, String path) {
        def textRenderer = mock(TextRenderer)
        when(textRenderer.render(any())).thenReturn(Result.of(text))
        def frontMatterGetter = mock(FrontMatterGetter)
        def excerptGetter = mock(ExcerptGetter)
        new Text(path, new TextType([], textRenderer, frontMatterGetter, excerptGetter), text)
    }

    private TextMocks() {}

}
