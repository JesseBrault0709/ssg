package com.jessebrault.ssg.text

import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class TextMocks {

    static Text blankText() {
        def textRenderer = mock(TextRenderer)
        def frontMatterGetter = mock(FrontMatterGetter)
        def excerptGetter = mock(ExcerptGetter)
        new Text('', '', new TextType([], textRenderer, frontMatterGetter, excerptGetter))
    }

    static Text renderableText(String text) {
        def textRenderer = mock(TextRenderer)
        when(textRenderer.render(any(), any())).thenReturn(new Tuple2<>([], text))
        def frontMatterGetter = mock(FrontMatterGetter)
        def excerptGetter = mock(ExcerptGetter)
        new Text(text, '', new TextType([], textRenderer, frontMatterGetter, excerptGetter))
    }

    static Text textWithPath(String path) {
        def textRenderer = mock(TextRenderer)
        def frontMatterGetter = mock(FrontMatterGetter)
        def excerptGetter = mock(ExcerptGetter)
        new Text('', path, new TextType([], textRenderer, frontMatterGetter, excerptGetter))
    }

    static Text renderableTextWithPath(String text, String path) {
        def textRenderer = mock(TextRenderer)
        when(textRenderer.render(any(), any())).thenReturn(new Tuple2<>([], text))
        def frontMatterGetter = mock(FrontMatterGetter)
        def excerptGetter = mock(ExcerptGetter)
        new Text(text, path, new TextType([], textRenderer, frontMatterGetter, excerptGetter))
    }

}
