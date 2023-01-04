package com.jessebrault.ssg

import com.jessebrault.ssg.pagetemplate.GspRenderer
import com.jessebrault.ssg.pagetemplate.PageTemplateType
import com.jessebrault.ssg.pagetemplate.PageTemplatesFactoryImpl
import com.jessebrault.ssg.text.MarkdownFrontMatterGetter
import com.jessebrault.ssg.text.MarkdownRenderer
import com.jessebrault.ssg.text.TextFileType
import com.jessebrault.ssg.text.TextFilesFactoryImpl
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

class StaticSiteGeneratorTests {

    private final StaticSiteGenerator ssg = new StaticSiteGeneratorImpl(new Config(
            textFileTypes: [new TextFileType(['.md'], new MarkdownRenderer(), new MarkdownFrontMatterGetter())],
            pageTemplateTypes: [new PageTemplateType(['.gsp'], new GspRenderer())],
            textFileFactoryGetter: { Config config -> new TextFilesFactoryImpl(config.textFileTypes) },
            pageTemplatesFactoryGetter: { Config config -> new PageTemplatesFactoryImpl(config.pageTemplateTypes) }
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
