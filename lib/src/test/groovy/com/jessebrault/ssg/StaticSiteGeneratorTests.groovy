package com.jessebrault.ssg

import com.jessebrault.ssg.frontmatter.MarkdownFrontMatterGetter
import com.jessebrault.ssg.renderer.GspRenderer
import com.jessebrault.ssg.template.TemplatesFactoryImpl
import com.jessebrault.ssg.textfile.TextFilesFactoryImpl
import com.jessebrault.ssg.textrenderer.MarkdownRenderer
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

class StaticSiteGeneratorTests {

    private static final Logger logger = LoggerFactory.getLogger(StaticSiteGeneratorTests)

    private final StaticSiteGenerator ssg = new StaticSiteGeneratorImpl(new Config(
            textFilesFactory: new TextFilesFactoryImpl(),
            templatesFactory: new TemplatesFactoryImpl(),
            markdownFrontMatterGetter: new MarkdownFrontMatterGetter(),
            markdownRenderer: new MarkdownRenderer(),
            gspRenderer: new GspRenderer()
    ))

    @Test
    void simple() {
        def buildDir = File.createTempDir()
        def textsDir = File.createTempDir()
        def templatesDir = File.createTempDir()

        new File(textsDir, 'test.md').write('---\ntemplate: test.gsp\n---\n**Hello, World!**')
        new File(templatesDir, 'test.gsp').write('<%= text %>')


        def spec = new SiteSpec(
                buildDir: buildDir,
                textsDir: textsDir,
                templatesDir: templatesDir
        )

        this.ssg.generate(spec)

        def outFile = new File(buildDir, 'test.html')
        assertTrue(outFile.exists())
        assertEquals('<p><strong>Hello, World!</strong></p>\n', outFile.text)
    }

    @Test
    void nested() {
        def buildDir = File.createTempDir()
        def textsDir = File.createTempDir()
        def templatesDir = File.createTempDir()

        new FileTreeBuilder(textsDir).with {
            dir('nested') {
                file('nested.md', '---\ntemplate: nested.gsp\n---\n**Hello, World!**')
            }
        }

        new File(templatesDir, 'nested.gsp').write('<%= text %>')

        this.ssg.generate(new SiteSpec(
                buildDir: buildDir,
                textsDir: textsDir,
                templatesDir: templatesDir
        ))

        def outFile = new File(new File(buildDir, 'nested'), 'nested.html')
        assertTrue(outFile.exists())
        assertEquals('<p><strong>Hello, World!</strong></p>\n', outFile.text)
    }

}
