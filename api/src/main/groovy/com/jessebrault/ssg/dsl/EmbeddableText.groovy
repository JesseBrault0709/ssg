package com.jessebrault.ssg.dsl

import com.jessebrault.ssg.text.FrontMatter
import com.jessebrault.ssg.text.Text
import com.jessebrault.ssg.util.Diagnostic
import groovy.transform.EqualsAndHashCode
import groovy.transform.Memoized
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

import java.util.function.Consumer

@TupleConstructor(includeFields = true, defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode(includeFields = true)
final class EmbeddableText {

    private final Text text
    private final Consumer<Collection<Diagnostic>> diagnosticsConsumer

    @Memoized
    String render() {
        def result = this.text.type.renderer.render(this.text)
        if (result.diagnostics.size() > 0) {
            this.diagnosticsConsumer.accept(result.diagnostics)
            ''
        } else {
            result.get()
        }
    }

    @Memoized
    FrontMatter getFrontMatter() {
        def result = this.text.type.frontMatterGetter.get(this.text)
        if (result.hasDiagnostics()) {
            this.diagnosticsConsumer.accept(result.diagnostics)
            new FrontMatter(this.text, [:])
        } else {
            result.get()
        }
    }

    @Memoized
    String getExcerpt(int limit) {
        def result = this.text.type.excerptGetter.getExcerpt(this.text, limit)
        if (result.hasDiagnostics()) {
            this.diagnosticsConsumer.accept(result.diagnostics)
            ''
        } else {
            result.get()
        }
    }

    String getPath() {
        this.text.path
    }

    @Override
    String toString() {
        "EmbeddableText(text: ${ this.text })"
    }

}
