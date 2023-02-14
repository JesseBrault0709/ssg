package com.jessebrault.ssg.text

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.mockito.Mockito.mock

class TextFileTextsProviderTests {

    private static final TextType markdownType = new TextType(
            ['.md'],
            mock(TextRenderer),
            mock(FrontMatterGetter),
            mock(ExcerptGetter)
    )

    private File textsDir
    private TextsProvider textsProvider

    @BeforeEach
    void beforeEach() {
        this.textsDir = File.createTempDir()
        this.textsProvider = new TextFileTextsProvider(this.textsDir, [markdownType])
    }

    @Test
    void findsFile() {
        new FileTreeBuilder(this.textsDir).file('test.md', '**Hello, World!**')

        def r = this.textsProvider.provide()
        assertEquals(1, r.size())
        def f0 = r[0]
        assertEquals('test.md', f0.path)
        assertEquals('**Hello, World!**', f0.text)
        assertEquals(markdownType, f0.type)
    }

    @Test
    void findsNestedFiles() {
        new FileTreeBuilder(this.textsDir).dir('nested') {
            file('nested.md', '**Hello!**')
        }

        def r = this.textsProvider.provide()
        assertEquals(1, r.size())
        def f0 = r[0]
        assertEquals('nested/nested.md', f0.path)
        assertEquals('**Hello!**', f0.text)
        assertEquals(markdownType, f0.type)
    }

    @Test
    void ignoresUnsupportedFile() {
        new FileTreeBuilder(this.textsDir).file('.ignored', 'Ignored!')

        def r = this.textsProvider.provide()
        assertEquals(0, r.size())
    }

}
