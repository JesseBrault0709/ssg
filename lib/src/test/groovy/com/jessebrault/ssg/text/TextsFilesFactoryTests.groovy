package com.jessebrault.ssg.text

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class TextsFilesFactoryTests {

    private final TextFilesFactory textFilesFactory = new TextFilesFactoryImpl()

    @Test
    void findsFile() {
        def baseDir = File.createTempDir()
        def ftb = new FileTreeBuilder(baseDir)
        ftb.file('test.md', '**Hello, World!**')

        def r = textFilesFactory.getTextFiles(baseDir)
        assertEquals(1, r.size())
        def f0 = r[0]
        assertEquals('test.md', f0.relativePath)
        assertEquals('**Hello, World!**', f0.file.text)
        assertEquals(TextFile.Type.MARKDOWN, f0.type)
    }

    @Test
    void findsNestedFiles() {
        def baseDir = File.createTempDir()
        def ftb = new FileTreeBuilder(baseDir)
        ftb.dir('nested') {
            file('nested.md', '**Hello!**')
        }

        def r = textFilesFactory.getTextFiles(baseDir)
        assertEquals(1, r.size())
        def f0 = r[0]
        assertEquals('nested/nested.md', f0.relativePath)
        assertEquals('**Hello!**', f0.file.text)
        assertEquals(TextFile.Type.MARKDOWN, f0.type)
    }

    @Test
    void ignoresUnsupportedFile() {
        def baseDir = File.createTempDir()
        def ftb = new FileTreeBuilder(baseDir)
        ftb.file('.ignored', 'Ignored!')

        def r = textFilesFactory.getTextFiles(baseDir)
        assertEquals(0, r.size())
    }

}
