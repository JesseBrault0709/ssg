package com.jessebrault.ssg.part

import groovy.transform.ToString

@ToString(includeFields = true)
class PartsMap {

    @Delegate
    private final Map<String, EmbeddablePart> partsMap = [:]

    PartsMap(Collection<Part> parts) {
        Objects.requireNonNull(parts)
        parts.each {
            this.put(it.name, new EmbeddablePart(it))
        }
    }

}
