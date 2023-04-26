package com.jessebrault.ssg.dsl

import com.jessebrault.ssg.dsl.tagbuilder.DynamicTagBuilder
import com.jessebrault.ssg.dsl.urlbuilder.PathBasedUrlBuilder
import com.jessebrault.ssg.render.RenderContext
import com.jessebrault.ssg.text.Text
import com.jessebrault.ssg.util.Diagnostic
import groovy.transform.NullCheck
import org.slf4j.LoggerFactory

import java.util.function.Consumer

final class StandardDslMap {

    @NullCheck(includeGenerated = true)
    static final class Builder {

        private final Map<String, Object> custom = [:]

        Consumer<Collection<Diagnostic>> diagnosticsConsumer = { }
        String loggerName = ''
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
            Consumer<Builder> builderConsumer
    ) {
        def b = new Builder()
        builderConsumer.accept(b)

        [:].tap {
            // standard variables
            it.globals = context.globals
            it.logger = LoggerFactory.getLogger(b.loggerName)
            it.models = new ModelCollection<Object>(context.models)
            it.parts = new EmbeddablePartsMap(
                    context,
                    b.diagnosticsConsumer,
                    b.text
            )
            it.siteSpec = context.siteSpec
            it.sourcePath = context.sourcePath
            it.tagBuilder = new DynamicTagBuilder()
            it.targetPath = context.targetPath
            it.tasks = new TaskCollection(context.tasks)
            it.text = b.text ? new EmbeddableText(
                    b.text,
                    b.diagnosticsConsumer
            ) : null
            it.texts = new EmbeddableTextsCollection(
                    context.texts,
                    b.diagnosticsConsumer
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
