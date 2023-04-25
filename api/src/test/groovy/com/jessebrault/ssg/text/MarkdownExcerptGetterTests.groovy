package com.jessebrault.ssg.text

import org.junit.jupiter.api.Test

import static com.jessebrault.ssg.text.TextMocks.nonRenderableText

final class MarkdownExcerptGetterTests extends AbstractExcerptGetterTests {

    final ExcerptGetter excerptGetter = new MarkdownExcerptGetter()

    @Test
    void worksWithHeading() {
        this.doTest(1, 'Heading') {
            nonRenderableText('# Heading\nOne Two Three')
        }
    }

    @Test
    void worksWithFrontMatter() {
        this.doTest(1, 'One') {
            nonRenderableText('---\ntest: hello\n---\nOne Two Three')
        }
    }

}
