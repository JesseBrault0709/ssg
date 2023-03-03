package com.jessebrault.ssg.part

import com.jessebrault.ssg.renderer.RenderContext
import com.jessebrault.ssg.text.Text
import groovy.transform.EqualsAndHashCode
import org.jetbrains.annotations.Nullable

import static java.util.Objects.requireNonNull

@EqualsAndHashCode(includeFields = true)
class EmbeddablePartsMap {

    @Delegate
    private final Map<String, EmbeddablePart> partsMap = [:]

    EmbeddablePartsMap(
            Collection<Part> parts,
            RenderContext context,
            Closure onDiagnostics,
            @Nullable Text text = null
    ) {
        requireNonNull(parts)
        requireNonNull(context)
        requireNonNull(onDiagnostics)
        parts.each {
            this.put(it.path, new EmbeddablePart(it, context, onDiagnostics, text))
        }
    }

    @Override
    String toString() {
        "EmbeddablePartsMap(partsMap: ${ this.partsMap })"
    }

}
