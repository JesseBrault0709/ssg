package com.jessebrault.ssg

import com.jessebrault.ssg.part.GspPartRenderer
import com.jessebrault.ssg.part.PartFilePartsProvider
import com.jessebrault.ssg.part.PartType
import com.jessebrault.ssg.specialpage.GspSpecialPageRenderer
import com.jessebrault.ssg.specialpage.SpecialPageFileSpecialPagesProvider
import com.jessebrault.ssg.specialpage.SpecialPageType
import com.jessebrault.ssg.template.GspTemplateRenderer
import com.jessebrault.ssg.template.TemplateType
import com.jessebrault.ssg.template.TemplateFileTemplatesProvider
import com.jessebrault.ssg.text.MarkdownFrontMatterGetter
import com.jessebrault.ssg.text.MarkdownTextRenderer
import com.jessebrault.ssg.text.TextType
import com.jessebrault.ssg.text.TextFileTextsProvider
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

        def config = new Config(
                textTypes: [new TextType(['.md'], new MarkdownTextRenderer(), new MarkdownFrontMatterGetter())],
                templateTypes: [new TemplateType(['.gsp'], new GspTemplateRenderer())],
                partTypes: [new PartType(['.gsp'], new GspPartRenderer())],
                specialPageTypes: [new SpecialPageType(['.gsp'], new GspSpecialPageRenderer())],

                textsDir: this.textsDir,
                templatesDir: this.templatesDir,
                partsDir: this.partsDir,
                specialPagesDir: this.specialPagesDir,

                textsProviderGetter: { Config config -> new TextFileTextsProvider(config.textTypes, config.textsDir) },
                templatesProviderGetter: { Config config -> new TemplateFileTemplatesProvider(config.templateTypes, config.templatesDir) },
                partsProviderGetter: { Config config -> new PartFilePartsProvider(config.partTypes, config.partsDir) },
                specialPagesProviderGetter: { Config config -> new SpecialPageFileSpecialPagesProvider(config.specialPageTypes, config.specialPagesDir) }
        )
        this.ssg = new SimpleStaticSiteGenerator(config)
    }

    @Test
    void simple() {
        new File(this.textsDir, 'test.md').write('---\ntemplate: test.gsp\n---\n**Hello, World!**')
        new File(this.templatesDir, 'test.gsp').write('<%= text %>')

        def buildDir = File.createTempDir()
        this.ssg.generate(buildDir)

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
        this.ssg.generate(buildDir)

        def outFile = new File(new File(buildDir, 'nested'), 'nested.html')
        assertTrue(outFile.exists())
        assertEquals('<p><strong>Hello, World!</strong></p>\n', outFile.text)
    }

    @Test
    void outputsSpecialPage() {
        new FileTreeBuilder(this.specialPagesDir).file('special.gsp', $/<%= texts['test'].render() %>/$)
        new FileTreeBuilder(this.templatesDir).file('template.gsp', '<%= 1 + 1 %>')
        new FileTreeBuilder(this.textsDir).file('test.md', '---\ntemplate: template.gsp\n---\nHello, World!')

        def buildDir = File.createTempDir()
        this.ssg.generate(buildDir)

        def outFile = new File(buildDir, 'special.html')
        assertTrue(outFile.exists())
        assertEquals('<p>Hello, World!</p>\n', outFile.text)
    }

}
