package com.jessebrault.ssg.part

import com.jessebrault.ssg.Diagnostic
import com.jessebrault.ssg.dsl.StandardDslMap
import com.jessebrault.ssg.renderer.RenderContext
import com.jessebrault.ssg.text.EmbeddableText
import com.jessebrault.ssg.text.Text
import groovy.text.GStringTemplateEngine
import groovy.text.TemplateEngine
import groovy.transform.EqualsAndHashCode
import org.jetbrains.annotations.Nullable

@EqualsAndHashCode
class GspPartRenderer implements PartRenderer {

    private static final TemplateEngine engine = new GStringTemplateEngine()

    @Override
    Tuple2<Collection<Diagnostic>, String> render(
            Part part,
            Map binding,
            RenderContext context,
            @Nullable Text text
    ) {
        Objects.requireNonNull(part)
        Objects.requireNonNull(binding)
        Objects.requireNonNull(context)
        def diagnostics = []
        try {
            def dslMap = StandardDslMap.get(context) {
                it.putCustom('binding', binding)
                it.loggerName = "GspPart(${ part.path })"
                it.onDiagnostics = diagnostics.&addAll
                if (text) {
                    it.text = text
                }
            }
            def result = engine.createTemplate(part.text).make(dslMap)
            new Tuple2<>(diagnostics, result.toString())
        } catch (Exception e) {
            new Tuple2<>(
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
