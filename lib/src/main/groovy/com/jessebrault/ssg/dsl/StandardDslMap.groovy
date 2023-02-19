package com.jessebrault.ssg.dsl

import com.jessebrault.ssg.part.EmbeddablePartsMap
import com.jessebrault.ssg.renderer.RenderContext
import com.jessebrault.ssg.text.EmbeddableText
import com.jessebrault.ssg.text.EmbeddableTextsCollection
import com.jessebrault.ssg.text.Text
import com.jessebrault.ssg.url.PathBasedUrlBuilder
import groovy.transform.NullCheck
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import org.slf4j.LoggerFactory

final class StandardDslMap {

    @NullCheck(includeGenerated = true)
    static final class Builder {

        private final Map custom = [:]

        String loggerName = ''
        Closure onDiagnostics = { }
        Text text = null

        void putCustom(key, value) {
            this.custom.put(key, value)
        }

        void putAllCustom(Map m) {
            this.custom.putAll(m)
        }

    }

    static Map get(
            RenderContext context,
            @DelegatesTo(value = Builder, strategy = Closure.DELEGATE_FIRST)
            @ClosureParams(
                    value = SimpleType,
                    options = ['com.jessebrault.ssg.dsl.StandardDslMap.Builder']
            )
            Closure builderClosure
    ) {
        def b = new Builder()
        builderClosure.resolveStrategy = Closure.DELEGATE_FIRST
        builderClosure.delegate = b
        builderClosure(b)

        [:].tap {
            it.globals = context.globals
            it.logger = LoggerFactory.getLogger(b.loggerName)
            it.parts = new EmbeddablePartsMap(
                    context.parts,
                    context,
                    b.onDiagnostics,
                    b.text
            )
            it.siteSpec = context.siteSpec
            it.sourcePath = context.sourcePath
            it.targetPath = context.targetPath
            it.text = b.text ? new EmbeddableText(
                    b.text,
                    context.globals,
                    b.onDiagnostics
            ) : null
            it.texts = new EmbeddableTextsCollection(
                    context.texts,
                    context.globals,
                    b.onDiagnostics
            )
            it.urlBuilder = new PathBasedUrlBuilder(
                    context.targetPath,
                    context.siteSpec.baseUrl
            )

            it.putAll(b.custom)
        }
    }

}
