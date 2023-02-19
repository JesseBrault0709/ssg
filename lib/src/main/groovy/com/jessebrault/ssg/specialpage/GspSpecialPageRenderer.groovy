package com.jessebrault.ssg.specialpage

import com.jessebrault.ssg.Diagnostic
import com.jessebrault.ssg.dsl.StandardDslMap
import com.jessebrault.ssg.renderer.RenderContext
import groovy.text.GStringTemplateEngine
import groovy.text.TemplateEngine
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck

@NullCheck
@EqualsAndHashCode
class GspSpecialPageRenderer implements SpecialPageRenderer {

    private static final TemplateEngine engine = new GStringTemplateEngine()

    @Override
    Tuple2<Collection<Diagnostic>, String> render(
            SpecialPage specialPage,
            RenderContext context
    ) {
        def diagnostics = []
        try {
            def dslMap = StandardDslMap.get(context) {
                it.loggerName = "GspSpecialPage(${ specialPage.path })"
                it.onDiagnostics = diagnostics.&addAll
            }
            def result = engine.createTemplate(specialPage.text).make(dslMap)
            new Tuple2<>(diagnostics, result.toString())
        } catch (Exception e) {
            new Tuple2<>(
                    [*diagnostics, new Diagnostic(
                            "An exception occurred while rendering specialPage ${ specialPage.path }:\n${ e }",
                            e
                    )],
                    ''
            )
        }
    }

    @Override
    String toString() {
        "GspSpecialPageRenderer()"
    }

}
