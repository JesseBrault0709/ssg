package com.jessebrault.ssg.text

import groovy.transform.Memoized
import groovy.transform.TupleConstructor

@TupleConstructor(includeFields = true, defaults = false)
 class EmbeddableText {

    private final Text text

    @Memoized
    String render() {
        this.text.type.renderer.render(this.text.text)
    }

    @Memoized
    FrontMatter getFrontMatter() {
        this.text.type.frontMatterGetter.get(this.text.text)
    }

}
