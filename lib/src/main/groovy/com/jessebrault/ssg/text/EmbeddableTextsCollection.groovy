package com.jessebrault.ssg.text

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck

@NullCheck
@EqualsAndHashCode(includeFields = true)
class EmbeddableTextsCollection {

    @Delegate
    private final Collection<EmbeddableText> embeddableTexts = []

    EmbeddableTextsCollection(Collection<Text> texts, Map globals, Closure onDiagnostics) {
        Objects.requireNonNull(texts).each {
            this << new EmbeddableText(it, globals, onDiagnostics)
        }
    }

    @Override
    String toString() {
        "EmbeddableTextsCollection(embeddableTexts: ${ this.embeddableTexts })"
    }

}
