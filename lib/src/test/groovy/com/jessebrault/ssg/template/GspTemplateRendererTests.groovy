package com.jessebrault.ssg.template

import com.jessebrault.ssg.Diagnostic
import com.jessebrault.ssg.dsl.StandardDslConsumerTests
import com.jessebrault.ssg.renderer.RenderContext
import com.jessebrault.ssg.text.Text
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

import static com.jessebrault.ssg.testutil.DiagnosticsUtil.getDiagnosticsMessageSupplier
import static com.jessebrault.ssg.testutil.RenderContextUtil.getRenderContext
import static com.jessebrault.ssg.text.TextMocks.blankText
import static com.jessebrault.ssg.text.TextMocks.renderableText
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

@ExtendWith(MockitoExtension)
class GspTemplateRendererTests implements StandardDslConsumerTests {

    private final TemplateRenderer renderer = new GspTemplateRenderer()

    private Tuple2<Collection<Diagnostic>, String> doRender(String scriptlet, Text text, RenderContext context) {
        this.renderer.render(new Template(scriptlet, '', null), text, context)
    }

    @Override
    Tuple2<Collection<Diagnostic>, String> render(String scriptlet, RenderContext context) {
        this.doRender(scriptlet, blankText(), context)
    }

    /**
     * TODO: refactor this and the super interface methods so that we can re-use rendering logic
     */
    @Test
    void textAvailableToRender() {
        def template = new Template('<%= text.render() %>', null, null)
        def r = this.renderer.render(
                template,
                renderableText('Hello, World!'),
                getRenderContext()
        )
        assertTrue(r.v1.isEmpty(), getDiagnosticsMessageSupplier(r.v1))
        assertEquals('Hello, World!', r.v2)
    }

}
