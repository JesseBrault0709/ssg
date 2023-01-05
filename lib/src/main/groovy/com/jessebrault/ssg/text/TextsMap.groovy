package com.jessebrault.ssg.text

import groovy.transform.ToString

@ToString(includeFields = true)
class TextsMap {

    @Delegate
    private final Map<String, EmbeddableText> textsMap = [:]

    TextsMap(Collection<Text> texts) {
        Objects.requireNonNull(texts)
        texts.each {
            this.put(it.path, new EmbeddableText(it))
        }
    }

}
