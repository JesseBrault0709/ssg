package com.jessebrault.ssg.dsl

import com.jessebrault.ssg.SiteSpec
import com.jessebrault.ssg.dsl.tagbuilder.TagBuilder
import com.jessebrault.ssg.dsl.urlbuilder.UrlBuilder
import com.jessebrault.ssg.html.TextToHtmlTask
import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.part.PartRenderer
import com.jessebrault.ssg.part.PartType
import com.jessebrault.ssg.render.RenderContext
import com.jessebrault.ssg.task.TaskSpec
import com.jessebrault.ssg.util.Result
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.junit.jupiter.MockitoExtension
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static com.jessebrault.ssg.task.PageToHtmlTaskMocks.blankPageToHtmlTask
import static com.jessebrault.ssg.task.TextToHtmlTaskMocks.blankTextToHtmlTask
import static com.jessebrault.ssg.template.TemplateMocks.blankTemplate
import static com.jessebrault.ssg.text.TextMocks.blankText
import static com.jessebrault.ssg.text.TextMocks.renderableTextWithPath
import static com.jessebrault.ssg.util.DiagnosticsUtil.assertEmptyDiagnostics
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.fail
import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.anyString
import static org.mockito.Mockito.*

@ExtendWith(MockitoExtension)
interface StandardDslConsumerTests {

    Result<String> render(String scriptlet, RenderContext context)

    default void checkResult(String expected, Result<String> result) {
        assertEmptyDiagnostics(result)
        assertEquals(expected, result.get())
    }

    default void doDslRenderTest(String expected, String scriptlet, RenderContext context = null) {
        this.checkResult(expected, this.render(scriptlet, context ?: new RenderContext()))
    }

    default void doDslAssertionTest(String scriptlet, RenderContext context = null) {
        Result<String> result = null
        try {
            result = this.render(scriptlet, context ?: new RenderContext())
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
                new RenderContext(
                        globals: [test: 'Hello, World!']
                )
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
                .thenReturn(Result.of('Hello, World!'))
        def part = new Part(
                'test.gsp',
                new PartType([], partRenderer),
                'Hello, World!'
        )
        this.doDslRenderTest(
                'Hello, World!',
                '<%= parts["test.gsp"].render() %>',
                new RenderContext(parts: [part])
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
        this.doDslRenderTest(
                'Test Site https://test.com',
                '<%= siteSpec.name + " " + siteSpec.baseUrl %>',
                new RenderContext(siteSpec: new SiteSpec('Test Site', 'https://test.com'))
        )
    }

    @Test
    default void sourcePathAvailable() {
        this.doDslAssertionTest(
                '<% assert sourcePath && sourcePath instanceof String %>',
                new RenderContext(sourcePath: 'test.md')
        )
    }

    @Test
    default void sourcePathRendersCorrectValue() {
        this.doDslRenderTest(
                'test.md',
                '<%= sourcePath %>',
                new RenderContext(sourcePath: 'test.md')
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
                new RenderContext(targetPath: 'test/test.html')
        )
    }

    @Test
    default void targetPathRendersCorrectValue() {
        this.doDslRenderTest(
                'test/test.html',
                '<%= targetPath %>',
                new RenderContext(targetPath: 'test/test.html')
        )
    }

    @Test
    default void tasksAvailable() {
        this.doDslAssertionTest("<% assert tasks != null && tasks instanceof ${ TaskCollection.name } %>")
    }

    @Test
    default void tasksFind() {
        def task = new TextToHtmlTask(
                'testHtml',
                TaskSpec.getEmpty(),
                blankText(),
                blankTemplate(),
                [],
                [],
                []
        )
        this.doDslAssertionTest(
                '<%= assert tasks.find { it.path == "testHtml" } != null %>',
                new RenderContext(tasks: [task])
        )
    }

    @Test
    default void tasksByType() {
        def t0 = blankTextToHtmlTask()
        def t1 = blankPageToHtmlTask()
        this.doDslAssertionTest(
                '<% assert tasks.size() == 2 && ' +
                        'tasks.byType(TextToHtmlTask).size() == 1 &&' +
                        'tasks.byType(PageToHtmlTask).size() == 1 %>',
                new RenderContext(tasks: [t0, t1])
        )
    }

    @Test
    default void taskTypesImported() {
        this.doDslAssertionTest('<%= assert Task && HtmlTask && ModelToHtmlTask && PageToHtmlTask && TextToHtmlTask %>')
    }

    @Test
    default void textsAvailable() {
        this.doDslAssertionTest("<% assert texts != null && texts instanceof ${ EmbeddableTextsCollection.name } %>")
    }

    @Test
    default void textsTextAvailableAndRenderable() {
        def testText = renderableTextWithPath('Hello, World!', 'test.md')
        this.doDslRenderTest(
                'Hello, World!',
                '<%= texts.find { it.path == "test.md" }.render() %>',
                new RenderContext(texts: [testText])
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
                new RenderContext(targetPath: 'test/test.html')
        )
    }

}
