package com.jessebrault.ssg.part

import com.jessebrault.ssg.text.EmbeddableText
import groovy.transform.EqualsAndHashCode
import org.jetbrains.annotations.Nullable

@EqualsAndHashCode(includeFields = true)
class EmbeddablePartsMap {

    @Delegate
    private final Map<String, EmbeddablePart> partsMap = [:]

    EmbeddablePartsMap(
            Collection<Part> parts,
            Map globals,
            Closure onDiagnostics,
            @Nullable EmbeddableText text = null
    ) {
        Objects.requireNonNull(parts)
        Objects.requireNonNull(globals)
        Objects.requireNonNull(onDiagnostics)
        parts.each {
            this.put(it.path, new EmbeddablePart(it, globals, onDiagnostics, text))
        }
    }

    @Override
    String toString() {
        "EmbeddablePartsMap(partsMap: ${ this.partsMap })"
    }

}
