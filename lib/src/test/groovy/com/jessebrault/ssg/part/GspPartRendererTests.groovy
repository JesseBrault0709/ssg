package com.jessebrault.ssg.part

import com.jessebrault.ssg.Diagnostic
import com.jessebrault.ssg.dsl.StandardDslConsumerTests
import com.jessebrault.ssg.renderer.RenderContext
import com.jessebrault.ssg.text.Text
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

import static com.jessebrault.ssg.testutil.DiagnosticsUtil.assertDiagnosticException
import static com.jessebrault.ssg.testutil.RenderContextUtil.getRenderContext
import static com.jessebrault.ssg.text.TextMocks.renderableText
import static org.junit.jupiter.api.Assertions.assertEquals

@ExtendWith(MockitoExtension)
class GspPartRendererTests implements StandardDslConsumerTests {

    private final PartRenderer renderer = new GspPartRenderer()

    private Tuple2<Collection<Diagnostic>, String> doRender(
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
    Tuple2<Collection<Diagnostic>, String> render(String scriptlet, RenderContext context) {
        this.doRender(scriptlet, context)
    }

    @Test
    void rendersWithBinding() {
        this.checkResult(
                'Hello, World!',
                this.doRender('<%= binding.greeting %>', getRenderContext(), [greeting: 'Hello, World!'])
        )
    }

    @Test
    void textAvailable() {
        this.checkResult('Hello, World!', this.renderer.render(
                new Part('', new PartType([], this.renderer), '<%= text.render() %>'),
                [:],
                getRenderContext(),
                renderableText('Hello, World!')
        ))
    }

    @Test
    void nestedPartDiagnosticBubblesUp() {
        def nestedProblemPart = new Part(
                'nestedProblem.gsp',
                new PartType([], this.renderer),
                '<% throw new RuntimeException() %>'
        )
        def callerPart = new Part(
                'caller.gsp',
                null,
                '<% parts["nestedProblem.gsp"].render() %>'
        )
        def r = this.renderer.render(
                callerPart,
                [:],
                getRenderContext(parts: [callerPart, nestedProblemPart]),
                null
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
                '<% throw new RuntimeException("nested problem exception") %>'
        )
        def callerPart = new Part(
                'caller.gsp',
                null,
                'Hello, World!<% parts["nestedProblem.gsp"].render() %>'
        )
        def r = this.renderer.render(
                callerPart,
                [:],
                getRenderContext(parts: [callerPart, nestedProblemPart]),
                null
        )
        assertEquals(1, r.v1.size())
        assertDiagnosticException(RuntimeException, r.v1[0]) { e ->
            assertEquals('nested problem exception', e.message, {
                def w = new StringWriter()
                e.printStackTrace(new PrintWriter(w))
                w.toString()
            })
        }
        assertEquals('Hello, World!', r.v2)
    }

}
