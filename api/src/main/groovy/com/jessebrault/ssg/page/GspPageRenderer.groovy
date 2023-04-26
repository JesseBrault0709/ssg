package com.jessebrault.ssg.page

import com.jessebrault.ssg.render.RenderContext
import com.jessebrault.ssg.render.StandardGspRenderer
import com.jessebrault.ssg.util.Diagnostic
import com.jessebrault.ssg.util.Result
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck

@NullCheck
@EqualsAndHashCode
final class GspPageRenderer implements PageRenderer {

    private final StandardGspRenderer gspRenderer = new StandardGspRenderer(this.class.classLoader)

    @Override
    Result<String> render(
            Page specialPage,
            RenderContext context
    ) {
        def diagnostics = []
        try {
            def result = this.gspRenderer.render(specialPage.text, context) {
                it.diagnosticsConsumer = diagnostics.&addAll
                it.loggerName = "GspSpecialPage(${ specialPage.path })"
            }
            Result.of(diagnostics, result.toString())
        } catch (Exception e) {
            Result.of(
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
