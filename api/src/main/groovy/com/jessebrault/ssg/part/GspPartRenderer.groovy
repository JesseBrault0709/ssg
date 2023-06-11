package com.jessebrault.ssg.part

import com.jessebrault.ssg.render.RenderContext
import com.jessebrault.ssg.render.StandardGspRenderer
import com.jessebrault.ssg.text.Text
import com.jessebrault.ssg.util.Diagnostic
import com.jessebrault.ssg.util.Result
import groovy.transform.EqualsAndHashCode
import org.jetbrains.annotations.Nullable

import static java.util.Objects.requireNonNull

@EqualsAndHashCode
final class GspPartRenderer implements PartRenderer {

    private final StandardGspRenderer gspRenderer = new StandardGspRenderer(this.class.classLoader)

    @Override
    Result<String> render(
            Part part,
            Map<String, Object> binding,
            RenderContext context,
            @Nullable Text text
    ) {
        requireNonNull(part)
        requireNonNull(binding)
        requireNonNull(context)
        def diagnostics = []
        def result = this.gspRenderer.render(part.text, context) {
            it.putCustom('binding', binding)
            it.diagnosticsConsumer = diagnostics.&addAll
            it.loggerName = "GspPart(${ part.path })"
            if (text) {
                it.text = text
            }
        }
        if (result.hasDiagnostics()) {
            Result.ofDiagnostics(diagnostics + result.diagnostics)
        } else {
            Result.of(diagnostics, result.get())
        }
    }

    @Override
    String toString() {
        "GspPartRenderer()"
    }

}
