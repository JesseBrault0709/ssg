package com.jessebrault.ssg.text

import groovy.transform.ToString

@ToString(includeFields = true)
class EmbeddableTextsCollection {

    @Delegate
    private final Collection<EmbeddableText> embeddableTexts = []

    EmbeddableTextsCollection(Collection<Text> texts) {
        Objects.requireNonNull(texts).each {
            this << new EmbeddableText(it)
        }
    }



}
