package com.jessebrault.ssg.part

import com.jessebrault.ssg.render.StandardGspRenderer
import com.jessebrault.ssg.util.Diagnostic
import com.jessebrault.ssg.render.RenderContext
import com.jessebrault.ssg.util.Result
import com.jessebrault.ssg.text.Text
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
        try {
            def result = this.gspRenderer.render(part.text, context) {
                it.putCustom('binding', binding)
                it.loggerName = "GspPart(${ part.path })"
                it.onDiagnostics = diagnostics.&addAll
                if (text) {
                    it.text = text
                }
                return
            }
            Result.of(diagnostics, result.toString())
        } catch (Exception e) {
            Result.of(
                    [*diagnostics, new Diagnostic(
                            "An exception occurred while rendering part ${ part.path }:\n${ e }",
                            e
                    )],
                    ''
            )
        }
    }

    @Override
    String toString() {
        "GspPartRenderer()"
    }

}
