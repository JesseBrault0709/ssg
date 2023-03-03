package com.jessebrault.ssg

import com.jessebrault.ssg.part.GspPartRenderer
import com.jessebrault.ssg.part.PartFilePartsProvider
import com.jessebrault.ssg.part.PartType
import com.jessebrault.ssg.specialpage.GspSpecialPageRenderer
import com.jessebrault.ssg.specialpage.SpecialPageFileSpecialPagesProvider
import com.jessebrault.ssg.specialpage.SpecialPageType
import com.jessebrault.ssg.task.SpecialPageToHtmlFileTask
import com.jessebrault.ssg.task.Task
import com.jessebrault.ssg.task.TaskTypeContainer
import com.jessebrault.ssg.task.TextToHtmlFileTask
import com.jessebrault.ssg.template.GspTemplateRenderer
import com.jessebrault.ssg.template.TemplateFileTemplatesProvider
import com.jessebrault.ssg.template.TemplateType
import com.jessebrault.ssg.text.MarkdownExcerptGetter
import com.jessebrault.ssg.text.MarkdownFrontMatterGetter
import com.jessebrault.ssg.text.MarkdownTextRenderer
import com.jessebrault.ssg.text.TextFileTextsProvider
import com.jessebrault.ssg.text.TextType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static com.jessebrault.ssg.testutil.DiagnosticsUtil.assertEmptyDiagnostics
import static com.jessebrault.ssg.testutil.DiagnosticsUtil.getDiagnosticsMessageSupplier
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

        def markdownTextType = new TextType(['.md'], new MarkdownTextRenderer(), new MarkdownFrontMatterGetter(), new MarkdownExcerptGetter())
        def gspTemplateType = new TemplateType(['.gsp'], new GspTemplateRenderer())
        def gspPartType = new PartType(['.gsp'], new GspPartRenderer())
        def gspSpecialPageType = new SpecialPageType(['.gsp'], new GspSpecialPageRenderer())

        def textsProvider = new TextFileTextsProvider(this.textsDir, [markdownTextType])
        def templatesProvider = new TemplateFileTemplatesProvider(this.templatesDir, [gspTemplateType])
        def partsProvider = new PartFilePartsProvider(this.partsDir, [gspPartType])
        def specialPagesProvider = new SpecialPageFileSpecialPagesProvider(this.specialPagesDir, [gspSpecialPageType])

        def config = new Config(
                textProviders: [textsProvider],
                templatesProviders: [templatesProvider],
                partsProviders: [partsProvider],
                specialPagesProviders: [specialPagesProvider]
        )
        def siteSpec = new SiteSpec('Test Site', 'https://test.com')
        def globals = [:]

        this.build = new Build('testBuild', config, siteSpec, globals, new File('build'))
        this.ssg = new SimpleStaticSiteGenerator()
    }

    @Test
    void simple() {
        new File(this.textsDir, 'test.md').write('---\ntemplate: test.gsp\n---\n**Hello, World!**')
        new File(this.templatesDir, 'test.gsp').write('<%= text.render() %>')

        def result = this.ssg.generate(this.build)

        assertEmptyDiagnostics(result)
        def tasks = result.get()
        assertTrue(tasks.size() == 1)

        def t0 = tasks.findAllByType(TextToHtmlFileTask.TYPE)[0]
        assertEquals('test.html', t0.output.htmlPath)
        def contentResult = t0.output.getContent(tasks, new TaskTypeContainer([TextToHtmlFileTask.TYPE])) { Collection<Diagnostic> diagnostics ->
            fail(getDiagnosticsMessageSupplier(diagnostics))
        }
        assertEquals('<p><strong>Hello, World!</strong></p>\n', contentResult)
    }

    @Test
    void nested() {
        new FileTreeBuilder(this.textsDir).with {
            dir('nested') {
                file('nested.md', '---\ntemplate: nested.gsp\n---\n**Hello, World!**')
            }
        }

        new File(this.templatesDir, 'nested.gsp').write('<%= text.render() %>')

        def result = this.ssg.generate(this.build)

        assertEmptyDiagnostics(result)
        def tasks = result.get()
        assertTrue(tasks.size() == 1)

        def t0 = tasks.findAllByType(TextToHtmlFileTask.TYPE)[0]
        assertEquals('nested/nested.html', t0.output.htmlPath)
        def contentResult = t0.output.getContent(
                tasks,
                new TaskTypeContainer([TextToHtmlFileTask.TYPE])
        ) { Collection<Diagnostic> diagnostics ->
            fail(getDiagnosticsMessageSupplier(diagnostics))
        }
        assertEquals('<p><strong>Hello, World!</strong></p>\n', contentResult)
    }

    @Test
    void outputsSpecialPage() {
        new FileTreeBuilder(this.specialPagesDir)
                .file('special.gsp', $/<%= texts.find { it.path == 'test.md' }.render() %>/$)
        new FileTreeBuilder(this.templatesDir).file('template.gsp', '<%= 1 + 1 %>')
        new FileTreeBuilder(this.textsDir).file('test.md', '---\ntemplate: template.gsp\n---\nHello, World!')

        def result = this.ssg.generate(this.build)

        assertEmptyDiagnostics(result)
        def tasks = result.get()
        assertEquals(2, tasks.size())

        def taskTypes = new TaskTypeContainer([TextToHtmlFileTask.TYPE, SpecialPageToHtmlFileTask.TYPE])

        def testPageTask = tasks.findAllByType(TextToHtmlFileTask.TYPE).find {
            it.output.htmlPath == 'test.html'
        }
        assertNotNull(testPageTask)
        def testPageContent = testPageTask.output.getContent(tasks, taskTypes) { Collection<Diagnostic> diagnostics ->
            fail(getDiagnosticsMessageSupplier(diagnostics))
        }
        assertEquals('2', testPageContent)

        def specialPageTask = tasks.findAllByType(SpecialPageToHtmlFileTask.TYPE).find {
            it.output.htmlPath == 'special.html'
        }
        assertNotNull(specialPageTask)
        def specialPageContent = specialPageTask.output.getContent(tasks, taskTypes) { Collection<Diagnostic> diagnostics ->
            fail(getDiagnosticsMessageSupplier(diagnostics))
        }
        assertEquals('<p>Hello, World!</p>\n', specialPageContent)
    }

    @Test
    void doesNotGenerateIfNoTemplateInFrontMatter() {
        new File(this.textsDir, 'test.md').write('Hello, World!')
        def result = this.ssg.generate(this.build)
        assertEmptyDiagnostics(result)
        assertEquals(0, result.get().size())
    }

}
