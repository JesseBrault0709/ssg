package com.jessebrault.ssg.part

import com.jessebrault.ssg.SiteSpec
import com.jessebrault.ssg.text.EmbeddableText
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.junit.jupiter.MockitoExtension
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static com.jessebrault.ssg.testutil.DiagnosticsUtil.assertDiagnosticException
import static com.jessebrault.ssg.testutil.DiagnosticsUtil.assertEmptyDiagnostics
import static com.jessebrault.ssg.testutil.DiagnosticsUtil.getDiagnosticsMessageSupplier
import static com.jessebrault.ssg.text.TextMocks.renderableText
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertInstanceOf
import static org.junit.jupiter.api.Assertions.assertTrue
import static org.mockito.ArgumentMatchers.anyString
import static org.mockito.Mockito.mockStatic
import static org.mockito.Mockito.verify

@ExtendWith(MockitoExtension)
class GspPartRendererTests {

    private final PartRenderer renderer = new GspPartRenderer()

    @Test
    void rendersWithNoBindingOrGlobals() {
        def part = new Part('', null, 'Hello, World!')
        def r = this.renderer.render(
                part,
                [:],
                new SiteSpec('', ''),
                [:],
                null,
                [part],
                '',
                ''
        )
        assertTrue(r.v1.size() == 0)
        assertEquals('Hello, World!', r.v2)
    }

    @Test
    void rendersWithBinding() {
        def part = new Part('', null, "<%= binding['greeting'] %>")
        def r = this.renderer.render(
                part,
                [greeting: 'Hello, World!'],
                new SiteSpec('', ''),
                [:],
                null,
                [part],
                '',
                ''
        )
        assertTrue(r.v1.size() == 0)
        assertEquals('Hello, World!', r.v2)
    }

    @Test
    void rendersWithGlobals() {
        def part = new Part(null, null, "<%= globals['greeting'] %>")
        def r = this.renderer.render(
                part,
                [:],
                new SiteSpec('', ''),
                [greeting: 'Hello, World!'],
                null,
                [part],
                '',
                ''
        )
        assertTrue(r.v1.size() == 0)
        assertEquals('Hello, World!', r.v2)
    }

    @Test
    void textAvailable() {
        def part = new Part('', null, '<%= text.render() %>')
        def textDiagnostics = []
        def r = this.renderer.render(
                part,
                [:],
                new SiteSpec('', ''),
                [:],
                new EmbeddableText(renderableText('Hello, World!'), [:], textDiagnostics.&addAll),
                [part],
                '',
                ''
        )
        assertTrue(textDiagnostics.isEmpty())
        assertTrue(r.v1.isEmpty())
        assertEquals('Hello, World!', r.v2)
    }

    @Test
    void tagBuilderAvailable() {
        def part = new Part('', null, '<%= tagBuilder.test() %>')
        def r = this.renderer.render(
                part,
                [:],
                new SiteSpec('', ''),
                [:],
                null,
                [part],
                '',
                ''
        )
        assertTrue(r.v1.isEmpty())
        assertEquals('<test />', r.v2)
    }

    @Test
    void allPartsAvailable() {
        def partType = new PartType(['.gsp'], this.renderer)
        def part0 = new Part('part0.gsp', partType, '<%= parts["part1.gsp"].render() %>')
        def part1 = new Part('part1.gsp', partType, 'Hello, World!')
        def r = this.renderer.render(
                part0,
                [:],
                new SiteSpec('', ''),
                [:],
                null,
                [part0, part1],
                '',
                ''
        )
        assertTrue(r.v1.isEmpty(), getDiagnosticsMessageSupplier(r.v1))
        assertEquals('Hello, World!', r.v2)
    }

    @Test
    void pathAvailableIfPresent() {
        def part = new Part('', null, '<%= path %>')
        def r = this.renderer.render(
                part,
                [:],
                new SiteSpec('', ''),
                [:],
                null,
                [part],
                'test.md',
                ''
        )
        assertEmptyDiagnostics(r)
        assertEquals('test.md', r.v2)
    }

    @Test
    void urlBuilderAvailable() {
        def part = new Part('', null, '<%= urlBuilder.relative("images/test.jpg") %>')
        def r = this.renderer.render(
                part,
                [:],
                new SiteSpec('', ''),
                [:],
                null,
                [part],
                '',
                'test/test.html'
        )
        assertEmptyDiagnostics(r)
        assertEquals('../images/test.jpg', r.v2)
    }

    @Test
    void targetPathAvailable() {
        def part = new Part('', null, '<%= targetPath %>')
        def r = this.renderer.render(
                part,
                [:],
                new SiteSpec('', ''),
                [:],
                null,
                [part],
                '',
                'test/test.html'
        )
        assertEmptyDiagnostics(r)
        assertEquals('test/test.html', r.v2)
    }

    @Test
    void siteSpecBaseUrlAvailable() {
        def part = new Part('', null, '<%= siteSpec.baseUrl %>')
        def r = this.renderer.render(
                part,
                [:],
                new SiteSpec('', 'https://test.com'),
                [:],
                null,
                [part],
                '',
                ''
        )
        assertEmptyDiagnostics(r)
        assertEquals('https://test.com', r.v2)
    }

    @Test
    void loggerAvailable(@Mock Logger logger) {
        try (MockedStatic<LoggerFactory> loggerFactory = mockStatic(LoggerFactory)) {
            loggerFactory.when { LoggerFactory.getLogger(anyString()) }
                    .thenReturn(logger)
            def part = new Part('', null, '<% logger.info "Hello, World!" %>')
            def r = this.renderer.render(
                    part,
                    [:],
                    new SiteSpec('', ''),
                    [:],
                    null,
                    [part],
                    '',
                    ''
            )
            assertEmptyDiagnostics(r)
            verify(logger).info('Hello, World!')
        }
    }

    @Test
    void nestedPartDiagnosticBubblesUp() {
        def nestedProblemPart = new Part(
                'nestedProblem.gsp',
                new PartType([], this.renderer),
                '<% throw new RuntimeException() %>'
        )
        def callerPart = new Part('caller.gsp', null, '<% parts["nestedProblem.gsp"].render() %>')
        def r = this.renderer.render(
                callerPart,
                [:],
                new SiteSpec('', ''),
                [:],
                null,
                [callerPart, nestedProblemPart],
                '',
                ''
        )
        assertEquals(1, r.v1.size())
        assertDiagnosticException(RuntimeException, r.v1[0])
        assertEquals('', r.v2)
    }

    @Test
    void nestedPartIsBlankWhenThrowingExceptionButCallerRendered() {
        def nestedProblemPart = new Part(
                'nestedProblem.gsp',
                new PartType([], this.renderer),
                '<% throw new RuntimeException() %>'
        )
        def callerPart = new Part(
                'caller.gsp',
                null,
                'Hello, World!<% parts["nestedProblem.gsp"].render() %>'
        )
        def r = this.renderer.render(
                callerPart,
                [:],
                new SiteSpec('', ''),
                [:],
                null,
                [callerPart, nestedProblemPart],
                '',
                ''
        )
        assertEquals(1, r.v1.size())
        assertDiagnosticException(RuntimeException, r.v1[0])
        assertEquals('Hello, World!', r.v2)
    }

}
