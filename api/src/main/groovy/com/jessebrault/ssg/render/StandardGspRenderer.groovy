package com.jessebrault.ssg.render

import com.jessebrault.ssg.dsl.StandardDslMap
import groovy.text.GStringTemplateEngine
import groovy.text.TemplateEngine
import org.codehaus.groovy.control.CompilerConfiguration

import java.util.function.Consumer

final class StandardGspRenderer {

    private final TemplateEngine engine

    StandardGspRenderer(ClassLoader parentClassLoader) {
        def cc = new CompilerConfiguration() // TODO: investigate if this makes any difference on the ultimate template
        def gcl = new GroovyClassLoader(parentClassLoader, cc)
        this.engine = new GStringTemplateEngine(gcl)
    }

    String render(
            String template,
            RenderContext context,
            Consumer<StandardDslMap.Builder> dslMapBuilderConsumer
    ) {
        this.engine.createTemplate(template).make(StandardDslMap.get(context, dslMapBuilderConsumer)).toString()
    }

}
