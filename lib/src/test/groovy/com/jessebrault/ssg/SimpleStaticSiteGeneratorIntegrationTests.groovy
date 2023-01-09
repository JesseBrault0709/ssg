package com.jessebrault.ssg

import com.jessebrault.ssg.part.GspPartRenderer
import com.jessebrault.ssg.part.PartFilePartsProvider
import com.jessebrault.ssg.part.PartType
import com.jessebrault.ssg.specialpage.GspSpecialPageRenderer
import com.jessebrault.ssg.specialpage.SpecialPageFileSpecialPagesProvider
import com.jessebrault.ssg.specialpage.SpecialPageType
import com.jessebrault.ssg.template.GspTemplateRenderer
import com.jessebrault.ssg.template.TemplateFileTemplatesProvider
import com.jessebrault.ssg.template.TemplateType
import com.jessebrault.ssg.text.MarkdownFrontMatterGetter
import com.jessebrault.ssg.text.MarkdownTextRenderer
import com.jessebrault.ssg.text.TextFileTextsProvider
import com.jessebrault.ssg.text.TextType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

class SimpleStaticSiteGeneratorIntegrationTests {

    private File partsDir
    private File templatesDir
    private File textsDir
    private File specialPagesDir

    private Build build
    private StaticSiteGenerator ssg

    @BeforeEach
    void beforeEach() {
        this.textsDir = File.createTempDir()
        this.templatesDir = File.createTempDir()
        this.partsDir = File.createTempDir()
        this.specialPagesDir = File.createTempDir()

        def markdownTextType = new TextType(['.md'], new MarkdownTextRenderer(), new MarkdownFrontMatterGetter())
        def gspTemplateType = new TemplateType(['.gsp'], new GspTemplateRenderer())
        def gspPartType = new PartType(['.gsp'], new GspPartRenderer())
        def gspSpecialPageType = new SpecialPageType(['.gsp'], new GspSpecialPageRenderer())

        def textsProvider = new TextFileTextsProvider([markdownTextType], this.textsDir)
        def templatesProvider = new TemplateFileTemplatesProvider([gspTemplateType], this.templatesDir)
        def partsProvider = new PartFilePartsProvider([gspPartType], this.partsDir)
        def specialPagesProvider = new SpecialPageFileSpecialPagesProvider([gspSpecialPageType], this.specialPagesDir)

        def config = new Config(
                textProviders: [textsProvider],
                templatesProviders: [templatesProvider],
                partsProviders: [partsProvider],
                specialPagesProviders: [specialPagesProvider]
        )
        def globals = [:]

        this.build = new Build('testBuild', config, globals, new File('build'))
        this.ssg = new SimpleStaticSiteGenerator()
    }

    @Test
    void simple() {
        new File(this.textsDir, 'test.md').write('---\ntemplate: test.gsp\n---\n**Hello, World!**')
        new File(this.templatesDir, 'test.gsp').write('<%= text %>')

        def result = this.ssg.generate(this.build)

        assertTrue(result.v1.size() == 0)
        assertTrue(result.v2.size() == 1)

        def p0 = result.v2[0]
        assertEquals('test', p0.path)
        assertEquals('<p><strong>Hello, World!</strong></p>\n', p0.html)
    }

    @Test
    void nested() {
        new FileTreeBuilder(this.textsDir).with {
            dir('nested') {
                file('nested.md', '---\ntemplate: nested.gsp\n---\n**Hello, World!**')
            }
        }

        new File(this.templatesDir, 'nested.gsp').write('<%= text %>')

        def result = this.ssg.generate(this.build)

        assertTrue(result.v1.size() == 0)
        assertTrue(result.v2.size() == 1)

        def p0 = result.v2[0]
        assertEquals('nested/nested', p0.path)
        assertEquals('<p><strong>Hello, World!</strong></p>\n', p0.html)
    }

    @Test
    void outputsSpecialPage() {
        new FileTreeBuilder(this.specialPagesDir).file('special.gsp', $/<%= texts.find { it.path == 'test' }.render() %>/$)
        new FileTreeBuilder(this.templatesDir).file('template.gsp', '<%= 1 + 1 %>')
        new FileTreeBuilder(this.textsDir).file('test.md', '---\ntemplate: template.gsp\n---\nHello, World!')

        def result = this.ssg.generate(this.build)

        assertEquals(0, result.v1.size())
        assertEquals(2, result.v2.size())

        def testPage = result.v2.find { it.path == 'test' }
        assertNotNull(testPage)
        assertEquals('2', testPage.html)

        def specialPage = result.v2.find { it.path == 'special' }
        assertNotNull(specialPage)
        assertEquals('<p>Hello, World!</p>\n', specialPage.html)
    }

}
