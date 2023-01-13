package com.jessebrault.ssg.text

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class ExcerptGetterTests {

    ExcerptGetter excerptGetter = new MarkdownExcerptGetter()

    @Test
    void takesAllIfTextLessThanLimit() {
        def text = new Text('One Two Three Four Five', null, null)
        def result = this.excerptGetter.getExcerpt(text, 10)
        assertEquals(0, result.v1.size())
        assertEquals('One Two Three Four Five', result.v2)
    }

    @Test
    void takesTheLimit() {
        def text = new Text('One Two Three Four Five', null, null)
        def result = this.excerptGetter.getExcerpt(text, 2)
        assertEquals(0, result.v1.size())
        assertEquals('One Two', result.v2)
    }

    @Test
    void worksWithHeading() {
        def text = new Text('# Heading\nOne Two Three', null, null)
        def result = this.excerptGetter.getExcerpt(text, 1)
        assertEquals(0, result.v1.size())
        assertEquals('Heading', result.v2)
    }

    @Test
    void worksWithFrontMatter() {
        def text = new Text('---\ntest: hello\n---\nOne Two Three', null, null)
        def result = this.excerptGetter.getExcerpt(text, 1)
        assertEquals(0, result.v1.size())
        assertEquals('One', result.v2)
    }

}
