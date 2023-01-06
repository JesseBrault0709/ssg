package com.jessebrault.ssg.part

import groovy.transform.ToString

@ToString(includeFields = true)
class EmbeddablePartsMap {

    @Delegate
    private final Map<String, EmbeddablePart> partsMap = [:]

    EmbeddablePartsMap(Collection<Part> parts) {
        Objects.requireNonNull(parts)
        parts.each {
            this.put(it.name, new EmbeddablePart(it))
        }
    }

}
