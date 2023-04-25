package com.jessebrault.ssg.dsl

import com.jessebrault.ssg.text.Text
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck

@NullCheck
@EqualsAndHashCode(includeFields = true)
final class EmbeddableTextsCollection {

    @Delegate
    private final Collection<EmbeddableText> embeddableTexts = []

    EmbeddableTextsCollection(Collection<Text> texts, Closure<Void> onDiagnostics) {
        texts.each {
            this << new EmbeddableText(it, onDiagnostics)
        }
    }

    @Override
    String toString() {
        "EmbeddableTextsCollection(embeddableTexts: ${ this.embeddableTexts })"
    }

}
