package com.jessebrault.ssg.text

import groovy.transform.EqualsAndHashCode
import groovy.transform.Memoized
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(includeFields = true, defaults = false)
@NullCheck
@EqualsAndHashCode(includeFields = true)
class EmbeddableText {

    private final Text text
    private final Map globals
    private final Closure onDiagnostics

    @Memoized
    String render() {
        def result = this.text.type.renderer.render(this.text, globals)
        if (result.v1.size() > 0) {
            this.onDiagnostics.call(result.v1)
            ''
        } else {
            result.v2
        }
    }

    @Memoized
    FrontMatter getFrontMatter() {
        def result = this.text.type.frontMatterGetter.get(this.text)
        if (result.v1.size() > 0) {
            this.onDiagnostics.call(result.v1)
            new FrontMatter(this.text, [:])
        } else {
            result.v2
        }
    }

    @Memoized
    String getExcerpt(int limit) {
        def result = this.text.type.excerptGetter.getExcerpt(this.text, limit)
        if (result.v1.size() > 0) {
            this.onDiagnostics.call(result.v1)
            ''
        } else {
            result.v2
        }
    }

    String getPath() {
        this.text.path
    }

    @Override
    String toString() {
        "EmbeddableText(text: ${ this.text }, globals: ${ this.globals })"
    }

}
