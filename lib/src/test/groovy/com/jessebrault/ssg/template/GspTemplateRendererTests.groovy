package com.jessebrault.ssg.template

import com.jessebrault.ssg.SiteSpec
import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.part.PartRenderer
import com.jessebrault.ssg.part.PartType
import com.jessebrault.ssg.text.FrontMatter
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.junit.jupiter.MockitoExtension
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static com.jessebrault.ssg.testutil.DiagnosticsUtil.assertEmptyDiagnostics
import static com.jessebrault.ssg.testutil.DiagnosticsUtil.getDiagnosticsMessageSupplier
import static com.jessebrault.ssg.text.TextMocks.*
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue
import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.anyString
import static org.mockito.ArgumentMatchers.argThat
import static org.mockito.Mockito.mockStatic
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

@ExtendWith(MockitoExtension)
class GspTemplateRendererTests {

    private final TemplateRenderer renderer = new GspTemplateRenderer()

    @Test
    void rendersPartWithNoBinding(@Mock PartRenderer partRenderer) {
        def template = new Template(
                "<%= parts['test'].render() %>",
                null,
                null
        )

        when(partRenderer.render(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new Tuple2<>([], 'Hello, World!'))
        def partType = new PartType([], partRenderer)
        def part = new Part('test', partType, null)

        def r = this.renderer.render(
                template,
                new FrontMatter(null, [:]),
                blankText(),
                [part],
                new SiteSpec('', ''),
                [:],
                ''
        )
        assertTrue(r.v1.isEmpty(), getDiagnosticsMessageSupplier(r.v1))
        assertEquals('Hello, World!', r.v2)
    }

    @Test
    void rendersPartWithBinding(@Mock PartRenderer partRenderer) {
        def template = new Template(
                "<%= parts['greeting'].render([person: 'World']) %>",
                null,
                null
        )

        when(partRenderer.render(
                any(),
                argThat { Map m -> m.get('person') == 'World' },
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
        )).thenReturn(new Tuple2<>([], 'Hello, World!'))

        def partType = new PartType([], partRenderer)
        def part = new Part('greeting', partType, null)

        def r = this.renderer.render(
                template,
                new FrontMatter(null, [:]),
                blankText(),
                [part],
                new SiteSpec('', ''),
                [:],
                ''
        )
        assertEmptyDiagnostics(r)
        assertEquals('Hello, World!', r.v2)
    }

    @Test
    void rendersFrontMatter() {
        def template = new Template("<%= frontMatter['title'] %>", null, null)
        def r = this.renderer.render(
                template,
                new FrontMatter(null, [title: ['Hello!']]),
                blankText(),
                [],
                new SiteSpec('', ''),
                [:],
                ''
        )
        assertEmptyDiagnostics(r)
        assertEquals('Hello!', r.v2)
    }

    @Test
    void rendersGlobal() {
        def template = new Template("<%= globals['test'] %>", null, null)
        def r = this.renderer.render(
                template,
                new FrontMatter(null, [:]),
                blankText(),
                [],
                new SiteSpec('', ''),
                [test: 'Hello, World!'],
                ''
        )
        assertEmptyDiagnostics(r)
        assertEquals('Hello, World!', r.v2)
    }

    @Test
    void textAvailableToRender() {
        def template = new Template('<%= text.render() %>', null, null)
        def r = this.renderer.render(
                template,
                new FrontMatter(null, [:]),
                renderableText('Hello, World!'),
                [],
                new SiteSpec('', ''),
                [:],
                ''
        )
        assertTrue(r.v1.isEmpty(), getDiagnosticsMessageSupplier(r.v1))
        assertEquals('Hello, World!', r.v2)
    }

    @Test
    void tagBuilderAvailable() {
        def template = new Template('<%= tagBuilder.test() %>', null, null)
        def r = this.renderer.render(
                template,
                new FrontMatter(null, [:]),
                blankText(),
                [],
                new SiteSpec('', ''),
                [:],
                ''
        )
        assertTrue(r.v1.isEmpty(), getDiagnosticsMessageSupplier(r.v1))
        assertEquals('<test />', r.v2)
    }

    @Test
    void pathAvailable() {
        def template = new Template('<%= path %>', null, null)
        def r = this.renderer.render(
                template,
                new FrontMatter(null, [:]),
                textWithPath('test.md'),
                [],
                new SiteSpec('', ''),
                [:],
                ''
        )
        assertTrue(r.v1.isEmpty(), getDiagnosticsMessageSupplier(r.v1))
        assertEquals('test.md', r.v2)
    }

    @Test
    void urlBuilderAvailable() {
        def template = new Template('<%= urlBuilder.relative("images/test.jpg") %>', null, null)
        def r = this.renderer.render(
                template,
                new FrontMatter(null, [:]),
                blankText(),
                [],
                new SiteSpec('', ''),
                [:],
                'test.html'
        )
        assertTrue(r.v1.isEmpty(), getDiagnosticsMessageSupplier(r.v1))
        assertEquals('images/test.jpg', r.v2)
    }

    @Test
    void urlBuilderIsRelativeToTargetPath() {
        def template = new Template('<%= urlBuilder.relative("images/test.jpg") %>', null, null)
        def r = this.renderer.render(
                template,
                new FrontMatter(null, [:]),
                blankText(),
                [],
                new SiteSpec('', ''),
                [:],
                'test/test.html'
        )
        assertEmptyDiagnostics(r)
        assertEquals('../images/test.jpg', r.v2)
    }

    @Test
    void urlBuilderAbsolutePathCorrect() {
        def template = new Template('<%= urlBuilder.absolute %>', null, null)
        def r = this.renderer.render(
                template,
                new FrontMatter(null, [:]),
                blankText(),
                [],
                new SiteSpec('', 'https://test.com'),
                [:],
                'test/test.html'
        )
        assertEmptyDiagnostics(r)
        assertEquals('https://test.com/test/test.html', r.v2)
    }

    @Test
    void targetPathAvailable() {
        def template = new Template('<%= targetPath %>', null, null)
        def r = this.renderer.render(
                template,
                new FrontMatter(null, [:]),
                blankText(),
                [],
                new SiteSpec('', ''),
                [:],
                'test.html'
        )
        assertEmptyDiagnostics(r)
        assertEquals('test.html', r.v2)
    }

    @Test
    void siteSpecBaseUrlAvailable() {
        def template = new Template('<%= siteSpec.baseUrl %>', null, null)
        def r = this.renderer.render(
                template,
                new FrontMatter(null, [:]),
                blankText(),
                [],
                new SiteSpec('', 'https://test.com'),
                [:],
                'test.html'
        )
        assertEmptyDiagnostics(r)
        assertEquals('https://test.com', r.v2)
    }

    @Test
    void loggerAvailable(@Mock Logger logger) {
        try (MockedStatic<LoggerFactory> loggerFactory = mockStatic(LoggerFactory)) {
            loggerFactory.when(() -> LoggerFactory.getLogger(anyString()))
                    .thenReturn(logger)
            def template = new Template('<% logger.info "Hello, World!" %>', null, null)
            def r = this.renderer.render(
                    template,
                    new FrontMatter(null, [:]),
                    blankText(),
                    [],
                    new SiteSpec('', ''),
                    [:],
                    ''
            )
            assertEmptyDiagnostics(r)
            verify(logger).info('Hello, World!')
        }
    }

}
