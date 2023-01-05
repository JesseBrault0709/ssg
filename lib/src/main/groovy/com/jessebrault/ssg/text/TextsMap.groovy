package com.jessebrault.ssg.text

class TextsMap {

    private final Map<String, EmbeddableText> textsMap = [:]

    TextsMap(Collection<Text> texts) {
        Objects.requireNonNull(texts)
        texts.each {
            this.textsMap.put(it.path, new EmbeddableText(it))
        }
    }

    EmbeddableText get(String path) {
        this.textsMap.get(Objects.requireNonNull(path))
    }

    EmbeddableText getAt(String path) {
        this.get(Objects.requireNonNull(path))
    }

}
