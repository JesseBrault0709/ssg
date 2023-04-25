package com.jessebrault.ssg.part

import com.jessebrault.ssg.dsl.StandardDslConsumerTests
import com.jessebrault.ssg.render.RenderContext
import com.jessebrault.ssg.text.Text
import com.jessebrault.ssg.util.Result
import org.junit.jupiter.api.Test

import static com.jessebrault.ssg.text.TextMocks.renderableText
import static com.jessebrault.ssg.util.DiagnosticsUtil.assertDiagnosticException
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

final class GspPartRendererTests implements StandardDslConsumerTests {

    private final PartRenderer renderer = new GspPartRenderer()

    private Result<String> doRender(
            String scriptlet,
            RenderContext context,
            Map binding = [:],
            Text text = null
    ) {
        this.renderer.render(
                new Part('', new PartType([], this.renderer), scriptlet),
                binding,
                context,
                text
        )
    }

    @Override
    Result<String> render(String scriptlet, RenderContext context) {
        this.doRender(scriptlet, context)
    }

    @Test
    void rendersWithBinding() {
        this.checkResult(
                'Hello, World!',
                this.doRender('<%= binding.greeting %>', new RenderContext(), [greeting: 'Hello, World!'])
        )
    }

    @Test
    void textAvailable() {
        this.checkResult('Hello, World!', this.renderer.render(
                new Part('', new PartType([], this.renderer), '<%= text.render() %>'),
                [:],
                new RenderContext(),
                renderableText('Hello, World!')
        ))
    }

    @Test
    void nestedPartDiagnosticBubblesUp() {
        def partType = new PartType([], this.renderer)
        def nestedProblemPart = new Part(
                'nestedProblem.gsp',
                partType,
                '<% throw new RuntimeException() %>'
        )
        def callerPart = new Part(
                'caller.gsp',
                partType,
                '<% parts["nestedProblem.gsp"].render() %>'
        )
        def r = this.renderer.render(
                callerPart,
                [:],
                new RenderContext(parts: [nestedProblemPart]),
                null
        )
        assertTrue(r.hasDiagnostics())
        assertEquals(1, r.diagnostics.size())
        assertDiagnosticException(RuntimeException, r.diagnostics[0])
        assertEquals('', r.get())
    }

    @Test
    void nestedPartIsBlankWhenThrowingExceptionButCallerRendered() {
        def partType = new PartType([], this.renderer)
        def nestedProblemPart = new Part(
                'nestedProblem.gsp',
                partType,
                '<% throw new RuntimeException("nested problem exception") %>'
        )
        def callerPart = new Part(
                'caller.gsp',
                partType,
                'Hello, World!<% parts["nestedProblem.gsp"].render() %>'
        )
        def r = this.renderer.render(
                callerPart,
                [:],
                new RenderContext(parts: [nestedProblemPart]),
                null
        )
        assertTrue(r.hasDiagnostics())
        assertEquals(1, r.diagnostics.size())
        assertDiagnosticException(RuntimeException, r.diagnostics[0]) { e ->
            assertEquals('nested problem exception', e.message, {
                def w = new StringWriter()
                e.printStackTrace(new PrintWriter(w))
                w.toString()
            })
        }
        assertEquals('Hello, World!', r.get())
    }

}
