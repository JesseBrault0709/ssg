package com.jessebrault.ssg.template

import com.jessebrault.ssg.render.StandardGspRenderer
import com.jessebrault.ssg.util.Diagnostic
import com.jessebrault.ssg.render.RenderContext
import com.jessebrault.ssg.util.Result
import com.jessebrault.ssg.text.Text
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck

@NullCheck
@EqualsAndHashCode
final class GspTemplateRenderer implements TemplateRenderer {

    private final StandardGspRenderer gspRenderer = new StandardGspRenderer(this.class.classLoader)

    @Override
    Result<String> render(
            Template template,
            Text text,
            RenderContext context
    ) {
        def diagnostics = []
        try {
            def result = this.gspRenderer.render(template.text, context) {
                it.loggerName = "GspTemplate(${ template.path })"
                it.onDiagnostics = diagnostics.&addAll
                it.text = text
                return
            }
            Result.of(diagnostics, result)
        } catch (Exception e) {
            Result.of(
                    [*diagnostics, new Diagnostic(
                            "An exception occurred while rendering Template ${ template.path }:\n${ e }",
                            e
                    )],
                    ''
            )
        }
    }

    @Override
    String toString() {
        "GspTemplateRenderer()"
    }

}
