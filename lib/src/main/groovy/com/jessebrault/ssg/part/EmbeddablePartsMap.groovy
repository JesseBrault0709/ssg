package com.jessebrault.ssg.part

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(includeFields = true)
class EmbeddablePartsMap {

    @Delegate
    private final Map<String, EmbeddablePart> partsMap = [:]

    EmbeddablePartsMap(Collection<Part> parts, Map globals) {
        Objects.requireNonNull(parts)
        parts.each {
            this.put(it.path, new EmbeddablePart(it, globals))
        }
    }

    @Override
    String toString() {
        "EmbeddablePartsMap(partsMap: ${ this.partsMap })"
    }

}
