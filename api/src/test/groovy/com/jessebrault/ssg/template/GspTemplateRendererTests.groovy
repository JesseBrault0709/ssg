package com.jessebrault.ssg.template

import com.jessebrault.ssg.dsl.StandardDslConsumerTests
import com.jessebrault.ssg.render.RenderContext
import com.jessebrault.ssg.text.Text
import com.jessebrault.ssg.util.Result
import org.junit.jupiter.api.Test

import static com.jessebrault.ssg.text.TextMocks.blankText
import static com.jessebrault.ssg.text.TextMocks.renderableText
import static com.jessebrault.ssg.util.DiagnosticsUtil.assertEmptyDiagnostics
import static org.junit.jupiter.api.Assertions.assertEquals

final class GspTemplateRendererTests implements StandardDslConsumerTests {

    private final TemplateRenderer renderer = new GspTemplateRenderer()

    private Result<String> doRender(String scriptlet, Text text, RenderContext context) {
        this.renderer.render(new Template('', new TemplateType([], this.renderer), scriptlet), text, context)
    }

    @Override
    Result<String> render(String scriptlet, RenderContext context) {
        this.doRender(scriptlet, blankText(), context)
    }

    @Test
    void textAvailableToRender() {
        def template = new Template('', new TemplateType([], this.renderer), '<%= text.render() %>')
        def r = this.renderer.render(
                template,
                renderableText('Hello, World!'),
                new RenderContext()
        )
        assertEmptyDiagnostics(r)
        assertEquals('Hello, World!', r.get())
    }

}
