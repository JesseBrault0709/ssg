package com.jessebrault.ssg.dsl

import com.jessebrault.ssg.dsl.tagbuilder.DynamicTagBuilder
import com.jessebrault.ssg.dsl.urlbuilder.PathBasedUrlBuilder
import com.jessebrault.ssg.render.RenderContext
import com.jessebrault.ssg.text.Text
import groovy.transform.NullCheck
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import org.slf4j.LoggerFactory

final class StandardDslMap {

    @NullCheck(includeGenerated = true)
    static final class Builder {

        private final Map<String, Object> custom = [:]

        String loggerName = ''
        Closure<Void> onDiagnostics = { }
        Text text = null

        void putCustom(String key, Object value) {
            this.custom.put(key, value)
        }

        void putAllCustom(Map<String, Object> m) {
            this.custom.putAll(m)
        }

    }

    static Map<String, Object> get(
            RenderContext context,
            @DelegatesTo(value = Builder, strategy = Closure.DELEGATE_FIRST)
            @ClosureParams(
                    value = SimpleType,
                    options = ['com.jessebrault.ssg.dsl.StandardDslMap.Builder']
            )
            Closure<Void> builderClosure
    ) {
        def b = new Builder()
        builderClosure.resolveStrategy = Closure.DELEGATE_FIRST
        builderClosure.delegate = b
        builderClosure(b)

        [:].tap {
            // standard variables
            it.globals = context.globals
            it.logger = LoggerFactory.getLogger(b.loggerName)
            it.models = new ModelCollection<Object>(context.models)
            it.parts = new EmbeddablePartsMap(
                    context,
                    b.onDiagnostics,
                    b.text
            )
            it.siteSpec = context.siteSpec
            it.sourcePath = context.sourcePath
            it.tagBuilder = new DynamicTagBuilder()
            it.targetPath = context.targetPath
            it.tasks = new TaskCollection(context.tasks)
            it.text = b.text ? new EmbeddableText(
                    b.text,
                    b.onDiagnostics
            ) : null
            it.texts = new EmbeddableTextsCollection(
                    context.texts,
                    b.onDiagnostics
            )
            it.urlBuilder = new PathBasedUrlBuilder(
                    context.targetPath,
                    context.siteSpec.baseUrl
            )

            // task types
            it.Task = com.jessebrault.ssg.task.Task
            it.HtmlTask = com.jessebrault.ssg.html.HtmlTask
            it.ModelToHtmlTask = com.jessebrault.ssg.html.ModelToHtmlTask
            it.PageToHtmlTask = com.jessebrault.ssg.html.PageToHtmlTask
            it.TextToHtmlTask = com.jessebrault.ssg.html.TextToHtmlTask

            // custom
            it.putAll(b.custom)
        }
    }

}
