package com.jessebrault.ssg.part

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck

@NullCheck
@EqualsAndHashCode(includeFields = true)
class EmbeddablePartsMap {

    @Delegate
    private final Map<String, EmbeddablePart> partsMap = [:]

    EmbeddablePartsMap(Collection<Part> parts, Map globals, Closure onDiagnostics) {
        parts.each {
            this.put(it.path, new EmbeddablePart(it, globals, onDiagnostics))
        }
    }

    @Override
    String toString() {
        "EmbeddablePartsMap(partsMap: ${ this.partsMap })"
    }

}
