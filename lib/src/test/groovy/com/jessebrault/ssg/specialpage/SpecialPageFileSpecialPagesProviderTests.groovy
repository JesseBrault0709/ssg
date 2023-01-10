package com.jessebrault.ssg.specialpage

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class SpecialPageFileSpecialPagesProviderTests {

    private static final SpecialPageType gspType = new SpecialPageType(['.gsp'], null)

    private File specialPagesDir
    private SpecialPagesProvider specialPagesProvider

    @BeforeEach
    void beforeEach() {
        this.specialPagesDir = File.createTempDir()
        this.specialPagesProvider = new SpecialPageFileSpecialPagesProvider([gspType], this.specialPagesDir)
    }

    @Test
    void findsSpecialPage() {
        new FileTreeBuilder(this.specialPagesDir)
                .file('test.gsp', '<%= "Hello, World!" %>')

        def r = this.specialPagesProvider.provide()
        assertEquals(1, r.size())
        def f0 = r[0]
        assertEquals('test', f0.path)
        assertEquals('<%= "Hello, World!" %>', f0.text)
        assertEquals(gspType, f0.type)
    }

    @Test
    void findsNestedSpecialPage() {
        new FileTreeBuilder(this.specialPagesDir).dir('nested') {
            file('nested.gsp', '<%= "Hello, World!" %>')
        }

        def r = this.specialPagesProvider.provide()
        assertEquals(1, r.size())
        def f0 = r[0]
        assertEquals('nested/nested', f0.path)
        assertEquals('<%= "Hello, World!" %>', f0.text)
        assertEquals(gspType, f0.type)
    }

    @Test
    void ignoresUnsupportedFile() {
        new FileTreeBuilder(this.specialPagesDir).file('.ignored', 'Ignored!')

        def r = this.specialPagesProvider.provide()
        assertEquals(0, r.size())
    }

}
