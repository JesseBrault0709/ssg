package com.jessebrault.ssg.dsl

import com.jessebrault.ssg.text.Text
import com.jessebrault.ssg.util.Diagnostic
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck

import java.util.function.Consumer

@NullCheck
@EqualsAndHashCode(includeFields = true)
final class EmbeddableTextsCollection {

    @Delegate
    private final Collection<EmbeddableText> embeddableTexts = []

    EmbeddableTextsCollection(Collection<Text> texts, Consumer<Collection<Diagnostic>> diagnosticsConsumer) {
        texts.each {
            this << new EmbeddableText(it, diagnosticsConsumer)
        }
    }

    @Override
    String toString() {
        "EmbeddableTextsCollection(embeddableTexts: ${ this.embeddableTexts })"
    }

}
