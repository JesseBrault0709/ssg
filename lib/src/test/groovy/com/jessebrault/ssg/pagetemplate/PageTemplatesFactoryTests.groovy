package com.jessebrault.ssg.pagetemplate

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class PageTemplatesFactoryTests {

    private static final PageTemplateType gspType = new PageTemplateType(['.gsp'], null)

    private final PageTemplatesFactory templateFactory = new PageTemplatesFactoryImpl([gspType])

    @Test
    void findsTemplate() {
        def templatesDir = File.createTempDir()
        def templateFile = new File(templatesDir, 'test.gsp')
        templateFile.write('<% out << text %>')

        def r = this.templateFactory.getTemplates(templatesDir)
        assertEquals(1, r.size())
        def t0 = r[0]
        assertEquals('test.gsp', t0.relativePath)
        assertEquals('<% out << text %>', t0.file.text)
        assertEquals(gspType, t0.type)
    }

    @Test
    void findsNestedTemplate() {
        def templatesDir = File.createTempDir()
        new FileTreeBuilder(templatesDir).with {
            dir('nested') {
                file('nested.gsp', '<%= text %>')
            }
        }

        def r = this.templateFactory.getTemplates(templatesDir)
        assertEquals(1, r.size())
        def t0 = r[0]
        assertEquals('nested/nested.gsp', t0.relativePath)
        assertEquals('<%= text %>', t0.file.text)
        assertEquals(gspType, t0.type)
    }

    @Test
    void ignoresUnsupportedFile() {
        def templatesDir = File.createTempDir()
        new File(templatesDir, '.ignored').with {
            write('Ignored!')
        }

        def r = this.templateFactory.getTemplates(templatesDir)
        assertEquals(0, r.size())
    }

}
