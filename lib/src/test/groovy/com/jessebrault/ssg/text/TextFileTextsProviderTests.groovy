package com.jessebrault.ssg.text

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class TextFileTextsProviderTests {

    private static final TextType markdownType = new TextType(['.md'], null, null)

    private File textsDir
    private TextsProvider textsProvider

    @BeforeEach
    void beforeEach() {
        this.textsDir = File.createTempDir()
        this.textsProvider = new TextFileTextsProvider([markdownType], this.textsDir)
    }

    @Test
    void findsFile() {
        new FileTreeBuilder(this.textsDir).file('test.md', '**Hello, World!**')

        def r = this.textsProvider.getTextFiles()
        assertEquals(1, r.size())
        def f0 = r[0]
        assertEquals('test', f0.path)
        assertEquals('**Hello, World!**', f0.text)
        assertEquals(markdownType, f0.type)
    }

    @Test
    void findsNestedFiles() {
        new FileTreeBuilder(this.textsDir).dir('nested') {
            file('nested.md', '**Hello!**')
        }

        def r = this.textsProvider.getTextFiles()
        assertEquals(1, r.size())
        def f0 = r[0]
        assertEquals('nested/nested', f0.path)
        assertEquals('**Hello!**', f0.text)
        assertEquals(markdownType, f0.type)
    }

    @Test
    void ignoresUnsupportedFile() {
        new FileTreeBuilder(this.textsDir).file('.ignored', 'Ignored!')

        def r = this.textsProvider.getTextFiles()
        assertEquals(0, r.size())
    }

}
