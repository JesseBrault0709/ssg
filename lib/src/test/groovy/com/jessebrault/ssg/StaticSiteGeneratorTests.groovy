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

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

class StaticSiteGeneratorTests {

    private File partsDir
    private File templatesDir
    private File textsDir
    private File specialPagesDir

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
        this.ssg = new SimpleStaticSiteGenerator(config)
    }

    @Test
    void simple() {
        new File(this.textsDir, 'test.md').write('---\ntemplate: test.gsp\n---\n**Hello, World!**')
        new File(this.templatesDir, 'test.gsp').write('<%= text %>')

        def buildDir = File.createTempDir()
        this.ssg.generate(buildDir, [:])

        def outFile = new File(buildDir, 'test.html')
        assertTrue(outFile.exists())
        assertEquals('<p><strong>Hello, World!</strong></p>\n', outFile.text)
    }

    @Test
    void nested() {
        new FileTreeBuilder(this.textsDir).with {
            dir('nested') {
                file('nested.md', '---\ntemplate: nested.gsp\n---\n**Hello, World!**')
            }
        }

        new File(this.templatesDir, 'nested.gsp').write('<%= text %>')

        def buildDir = File.createTempDir()
        this.ssg.generate(buildDir, [:])

        def outFile = new File(new File(buildDir, 'nested'), 'nested.html')
        assertTrue(outFile.exists())
        assertEquals('<p><strong>Hello, World!</strong></p>\n', outFile.text)
    }

    @Test
    void outputsSpecialPage() {
        new FileTreeBuilder(this.specialPagesDir).file('special.gsp', $/<%= texts.find { it.path == 'test' }.render() %>/$)
        new FileTreeBuilder(this.templatesDir).file('template.gsp', '<%= 1 + 1 %>')
        new FileTreeBuilder(this.textsDir).file('test.md', '---\ntemplate: template.gsp\n---\nHello, World!')

        def buildDir = File.createTempDir()
        this.ssg.generate(buildDir, [:])

        def outFile = new File(buildDir, 'special.html')
        assertTrue(outFile.exists())
        assertEquals('<p>Hello, World!</p>\n', outFile.text)
    }

}
