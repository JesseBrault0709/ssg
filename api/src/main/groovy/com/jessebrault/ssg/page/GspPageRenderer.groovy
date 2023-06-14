package com.jessebrault.ssg.page

import com.jessebrault.ssg.render.RenderContext
import com.jessebrault.ssg.render.StandardGspRenderer
import com.jessebrault.ssg.util.Result
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck

@NullCheck
@EqualsAndHashCode
final class GspPageRenderer implements PageRenderer {

    private final StandardGspRenderer gspRenderer

    GspPageRenderer(ClassLoader parentClassLoader) {
        this.gspRenderer = new StandardGspRenderer(parentClassLoader)
    }

    @Override
    Result<String> render(
            Page specialPage,
            RenderContext context
    ) {
        def diagnostics = []
        def result = this.gspRenderer.render(specialPage.text, context) {
            it.diagnosticsConsumer = diagnostics.&addAll
            it.loggerName = "GspSpecialPage(${ specialPage.path })"
        }
        if (result.hasDiagnostics()) {
            Result.ofDiagnostics(diagnostics + result.diagnostics)
        } else {
            Result.of(diagnostics, result.get())
        }
    }

    @Override
    String toString() {
        "GspSpecialPageRenderer()"
    }

}
