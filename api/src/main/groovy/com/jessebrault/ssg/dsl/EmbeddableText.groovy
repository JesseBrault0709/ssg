package com.jessebrault.ssg.dsl

import com.jessebrault.ssg.text.FrontMatter
import com.jessebrault.ssg.text.Text
import groovy.transform.EqualsAndHashCode
import groovy.transform.Memoized
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(includeFields = true, defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode(includeFields = true)
final class EmbeddableText {

    private final Text text
    private final Closure<Void> onDiagnostics

    @Memoized
    String render() {
        def result = this.text.type.renderer.render(this.text)
        if (result.diagnostics.size() > 0) {
            this.onDiagnostics.call(result.diagnostics)
            ''
        } else {
            result.get()
        }
    }

    @Memoized
    FrontMatter getFrontMatter() {
        def result = this.text.type.frontMatterGetter.get(this.text)
        if (result.hasDiagnostics()) {
            this.onDiagnostics.call(result.diagnostics)
            new FrontMatter(this.text, [:])
        } else {
            result.get()
        }
    }

    @Memoized
    String getExcerpt(int limit) {
        def result = this.text.type.excerptGetter.getExcerpt(this.text, limit)
        if (result.hasDiagnostics()) {
            this.onDiagnostics.call(result.diagnostics)
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
