package com.jessebrault.ssg.dsl

import com.jessebrault.ssg.Diagnostic
import com.jessebrault.ssg.SiteSpec
import com.jessebrault.ssg.part.EmbeddablePartsMap
import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.part.PartRenderer
import com.jessebrault.ssg.part.PartType
import com.jessebrault.ssg.renderer.RenderContext
import com.jessebrault.ssg.tagbuilder.TagBuilder
import com.jessebrault.ssg.task.HtmlFileOutput
import com.jessebrault.ssg.task.SpecialPageToHtmlFileTask
import com.jessebrault.ssg.task.TaskContainer
import com.jessebrault.ssg.task.TaskTypeContainer
import com.jessebrault.ssg.task.TextToHtmlFileTask
import com.jessebrault.ssg.task.TextToHtmlFileTaskFactory
import com.jessebrault.ssg.text.EmbeddableTextsCollection
import com.jessebrault.ssg.url.UrlBuilder
import net.bytebuddy.implementation.bytecode.Throw
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.junit.jupiter.MockitoExtension
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static com.jessebrault.ssg.task.SpecialPageToHtmlFileTaskMocks.blankSpecialPageToHtmlFileTask
import static com.jessebrault.ssg.task.TextToHtmlFileTaskMocks.blankTextToHtmlFileTask
import static com.jessebrault.ssg.testutil.DiagnosticsUtil.assertEmptyDiagnostics
import static com.jessebrault.ssg.testutil.RenderContextUtil.getRenderContext
import static com.jessebrault.ssg.text.TextMocks.blankText
import static com.jessebrault.ssg.text.TextMocks.renderableTextWithPath
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.fail
import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.anyString
import static org.mockito.Mockito.*

@ExtendWith(MockitoExtension)
interface StandardDslConsumerTests {

    Tuple2<Collection<Diagnostic>, String> render(String scriptlet, RenderContext context)

    default void checkResult(String expected, Tuple2<Collection<Diagnostic>, String> result) {
        assertEmptyDiagnostics(result)
        assertEquals(expected, result.v2)
    }

    default void doDslRenderTest(String expected, String scriptlet, RenderContext context = null) {
        this.checkResult(expected, this.render(scriptlet, context ?: getRenderContext()))
    }

    default void doDslAssertionTest(String scriptlet, RenderContext context = null) {
        Tuple2<Collection<Diagnostic>, String> result = null
        try {
            result = this.render(scriptlet, context ?: getRenderContext())
        } catch (Throwable e) {
            fail(e)
        }
        assertEmptyDiagnostics(result)
    }

    @Test
    default void rendersGlobal() {
        this.doDslRenderTest(
                'Hello, World!',
                '<%= globals.test %>',
                getRenderContext(globals: [test: 'Hello, World!'])
        )
    }

    @Test
    default void loggerAvailable(@Mock Logger logger) {
        try (MockedStatic<LoggerFactory> loggerFactory = mockStatic(LoggerFactory)) {
            loggerFactory.when { LoggerFactory.getLogger(anyString()) }
                    .thenReturn(logger)

            this.doDslAssertionTest('<% assert logger; logger.info("Hello, World!") %>')
            verify(logger).info('Hello, World!')
        }
    }

    @Test
    default void partsAvailable() {
        this.doDslAssertionTest("<% assert parts != null && parts instanceof ${ EmbeddablePartsMap.name } %>")
    }

    @Test
    default void partAvailableAndRenderable(@Mock PartRenderer partRenderer) {
        when(partRenderer.render(any(), any(), any(), any()))
                .thenReturn(new Tuple2<>([], 'Hello, World!'))
        def part = new Part(
                'test.gsp',
                new PartType([], partRenderer),
                'Hello, World!'
        )
        this.doDslRenderTest(
                'Hello, World!',
                '<%= parts["test.gsp"].render() %>',
                getRenderContext(parts: [part])
        )
    }

    @Test
    default void siteSpecAvailable() {
        this.doDslAssertionTest(
                "<% assert siteSpec && siteSpec instanceof ${ SiteSpec.name } %>"
        )
    }

    @Test
    default void siteSpecRendersCorrectValues() {
        def siteSpec = new SiteSpec('Test Site', 'https://test.com')
        this.doDslRenderTest(
                'Test Site https://test.com',
                '<%= siteSpec.name + " " + siteSpec.baseUrl %>',
                getRenderContext(siteSpec: siteSpec)
        )
    }

    @Test
    default void sourcePathAvailable() {
        this.doDslAssertionTest(
                '<% assert sourcePath && sourcePath instanceof String %>',
                getRenderContext(sourcePath: 'test.md')
        )
    }

    @Test
    default void sourcePathRendersCorrectValue() {
        this.doDslRenderTest(
                'test.md',
                '<%= sourcePath %>',
                getRenderContext(sourcePath: 'test.md')
        )
    }

    @Test
    default void tagBuilderAvailable() {
        this.doDslAssertionTest("<% assert tagBuilder && tagBuilder instanceof ${ TagBuilder.name } %>")
    }

    @Test
    default void targetPathAvailable() {
        this.doDslAssertionTest(
                '<% assert targetPath && targetPath instanceof String %>',
                getRenderContext(targetPath: 'test/test.html')
        )
    }

    @Test
    default void targetPathRendersCorrectValue() {
        this.doDslRenderTest(
                'test/test.html',
                '<%= targetPath %>',
                getRenderContext(targetPath: 'test/test.html')
        )
    }

    @Test
    default void tasksAvailable() {
        this.doDslAssertionTest("<% assert tasks != null && tasks instanceof ${ TaskContainer.name } %>")
    }

    @Test
    default void tasksFind() {
        def task = new TextToHtmlFileTask(
                'testTask',
                blankText(),
                new HtmlFileOutput(
                        new File('test.html'),
                        'test.html',
                        { '' }
                )
        )
        this.doDslRenderTest(
                'test.html',
                '<%= tasks.find { it.name == "testTask" }.output.htmlPath %>',
                getRenderContext(
                        tasks: new TaskContainer([task]),
                        taskTypes: new TaskTypeContainer([TextToHtmlFileTask.TYPE])
                )
        )
    }

    @Test
    default void taskTypesAvailable() {
        this.doDslAssertionTest(
                "<% assert taskTypes != null && taskTypes instanceof ${ TaskTypeContainer.name } %>"
        )
    }

    @Test
    default void taskFindAllByType() {
        def t0 = blankTextToHtmlFileTask()
        def t1 = blankSpecialPageToHtmlFileTask()
        this.doDslAssertionTest(
                '<% assert tasks.size() == 2 && ' +
                        'tasks.findAllByType(taskTypes.textToHtmlFile).size() == 1 &&' +
                        'tasks.findAllByType(taskTypes.specialPageToHtmlFile).size() == 1 %>',
                getRenderContext(
                        tasks: new TaskContainer([t0, t1]),
                        taskTypes: new TaskTypeContainer([TextToHtmlFileTask.TYPE, SpecialPageToHtmlFileTask.TYPE])
                )
        )
    }

    @Test
    default void textsAvailable() {
        this.doDslAssertionTest(
                "<% assert texts != null && texts instanceof ${ EmbeddableTextsCollection.name } %>"
        )
    }

    @Test
    default void textsTextAvailableAndRenderable() {
        def testText = renderableTextWithPath('Hello, World!', 'test.md')
        this.doDslRenderTest(
                'Hello, World!',
                '<%= texts.find { it.path == "test.md" }.render() %>',
                getRenderContext(texts: [testText])
        )
    }

    @Test
    default void urlBuilderAvailable() {
        this.doDslAssertionTest("<% assert urlBuilder && urlBuilder instanceof ${ UrlBuilder.name } %>")
    }

    @Test
    default void urlBuilderCorrectlyConfigured() {
        this.doDslRenderTest(
                '../images/test.jpg',
                '<%= urlBuilder.relative("images/test.jpg") %>',
                getRenderContext(targetPath: 'test/test.html')
        )
    }

}
