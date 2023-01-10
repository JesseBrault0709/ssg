package com.jessebrault.ssg.template

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class PageTemplatesProviderTests {

    private static final TemplateType gspType = new TemplateType(['.gsp'], null)

    private File templatesDir
    private TemplatesProvider templatesProvider

    @BeforeEach
    void beforeEach() {
        this.templatesDir = File.createTempDir()
        this.templatesProvider =  new TemplateFileTemplatesProvider([gspType], this.templatesDir)
    }

    @Test
    void findsTemplate() {
        new File(this.templatesDir, 'test.gsp').write('<% out << text %>')

        def r = this.templatesProvider.provide()
        assertEquals(1, r.size())
        def t0 = r[0]
        assertEquals('test.gsp', t0.path)
        assertEquals('<% out << text %>', t0.text)
        assertEquals(gspType, t0.type)
    }

    @Test
    void findsNestedTemplate() {
        new FileTreeBuilder(this.templatesDir).with {
            dir('nested') {
                file('nested.gsp', '<%= text %>')
            }
        }

        def r = this.templatesProvider.provide()
        assertEquals(1, r.size())
        def t0 = r[0]
        assertEquals('nested/nested.gsp', t0.path)
        assertEquals('<%= text %>', t0.text)
        assertEquals(gspType, t0.type)
    }

    @Test
    void ignoresUnsupportedFile() {
        new File(this.templatesDir, '.ignored').with {
            write('Ignored!')
        }

        def r = this.templatesProvider.provide()
        assertEquals(0, r.size())
    }

}
