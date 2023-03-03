package com.jessebrault.ssg.text

import org.junit.jupiter.api.Test

import static com.jessebrault.ssg.text.TextMocks.renderableText
import static org.junit.jupiter.api.Assertions.assertEquals

class ExcerptGetterTests {

    ExcerptGetter excerptGetter = new MarkdownExcerptGetter()

    @Test
    void takesAllIfTextLessThanLimit() {
        def text = renderableText('One Two Three Four Five')
        def result = this.excerptGetter.getExcerpt(text, 10)
        assertEquals(0, result.v1.size())
        assertEquals('One Two Three Four Five', result.v2)
    }

    @Test
    void takesTheLimit() {
        def text = renderableText('One Two Three Four Five')
        def result = this.excerptGetter.getExcerpt(text, 2)
        assertEquals(0, result.v1.size())
        assertEquals('One Two', result.v2)
    }

    @Test
    void worksWithHeading() {
        def text = renderableText('# Heading\nOne Two Three')
        def result = this.excerptGetter.getExcerpt(text, 1)
        assertEquals(0, result.v1.size())
        assertEquals('Heading', result.v2)
    }

    @Test
    void worksWithFrontMatter() {
        def text = renderableText('---\ntest: hello\n---\nOne Two Three')
        def result = this.excerptGetter.getExcerpt(text, 1)
        assertEquals(0, result.v1.size())
        assertEquals('One', result.v2)
    }

}
