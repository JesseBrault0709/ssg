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

    private static TemplateRenderer getRenderer(ClassLoader parentClassLoader = GspTemplateRendererTests.classLoader) {
        new GspTemplateRenderer(parentClassLoader)
    }

    private static Result<String> doRender(
            String scriptlet,
            Text text,
            RenderContext context,
            ClassLoader classLoader
    ) {
        def renderer = getRenderer(classLoader)
        renderer.render(new Template('', new TemplateType([], renderer), scriptlet), text, context)
    }

    @Override
    Result<String> render(String scriptlet, RenderContext context, ClassLoader classLoader) {
        doRender(scriptlet, blankText(), context, classLoader)
    }

    @Test
    void textAvailableToRender() {
        def renderer = getRenderer()
        def template = new Template('', new TemplateType([], renderer), '<%= text.render() %>')
        def r = renderer.render(
                template,
                renderableText('Hello, World!'),
                new RenderContext()
        )
        assertEmptyDiagnostics(r)
        assertEquals('Hello, World!', r.get())
    }

}
