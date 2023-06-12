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

    private final StandardGspRenderer gspRenderer

    GspTemplateRenderer(File tmpDir, GroovyScriptEngine engine) {
        this.gspRenderer = new StandardGspRenderer(tmpDir, engine)
    }

    @Override
    Result<String> render(
            Template template,
            Text text,
            RenderContext context
    ) {
        def diagnostics = []
        def result = this.gspRenderer.render(template.text, context) {
            it.diagnosticsConsumer = diagnostics.&addAll
            it.loggerName = "GspTemplate(${ template.path })"
            it.text = text
        }
        if (result.hasDiagnostics()) {
            Result.ofDiagnostics(diagnostics + result.diagnostics)
        } else {
            Result.of(diagnostics, result.get())
        }
    }

    @Override
    String toString() {
        "GspTemplateRenderer()"
    }

}
