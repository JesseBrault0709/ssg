package com.jessebrault.ssg.render

import com.jessebrault.gst.TemplateCreator
import com.jessebrault.gst.groovy.GroovyTemplateCreator
import com.jessebrault.gst.parser.ExtendedGstParser
import com.jessebrault.ssg.dsl.StandardDslMap
import com.jessebrault.ssg.util.Diagnostic
import com.jessebrault.ssg.util.Result

import java.util.function.Consumer

final class StandardGspRenderer {

    private final TemplateCreator templateCreator

    StandardGspRenderer(ClassLoader parentClassLoader) {
        this.templateCreator = new GroovyTemplateCreator(ExtendedGstParser::new, [], parentClassLoader, true)
    }

    Result<String> render(
            String templateText,
            RenderContext context,
            Consumer<StandardDslMap.Builder> dslMapBuilderConsumer
    ) {
        try {
            def templateCreateResult = this.templateCreator.create(templateText)
            if (templateCreateResult.hasDiagnostics()) {
                Result.ofDiagnostics(templateCreateResult.diagnostics.collect {
                    new Diagnostic(it.message, it.exception)
                })
            } else {
                Result.of(templateCreateResult.get().make(StandardDslMap.get(context, dslMapBuilderConsumer)))
            }
        } catch (Exception e) {
            Result.ofDiagnostics([new Diagnostic(e.message, e)])
        }
    }

}
