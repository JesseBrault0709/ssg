package com.jessebrault.ssg.template

import com.jessebrault.ssg.Diagnostic
import com.jessebrault.ssg.dsl.StandardDslMap
import com.jessebrault.ssg.renderer.RenderContext
import com.jessebrault.ssg.text.Text
import groovy.text.GStringTemplateEngine
import groovy.text.TemplateEngine
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck

@NullCheck
@EqualsAndHashCode
class GspTemplateRenderer implements TemplateRenderer {

    private static final TemplateEngine engine = new GStringTemplateEngine()

    @Override
    Tuple2<Collection<Diagnostic>, String> render(
            Template template,
            Text text,
            RenderContext context
    ) {
        def diagnostics = []
        try {
            def dslMap = StandardDslMap.get(context) {
                it.loggerName = "GspTemplate(${ template.path })"
                it.onDiagnostics = diagnostics.&addAll
                it.text = text
            }
            def result = engine.createTemplate(template.text).make(dslMap)
            new Tuple2<>(diagnostics, result.toString())
        } catch (Exception e) {
            new Tuple2<>(
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
