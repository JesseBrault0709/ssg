package com.jessebrault.ssg.dsl

import com.jessebrault.ssg.render.RenderContext
import com.jessebrault.ssg.text.Text
import groovy.transform.EqualsAndHashCode
import org.jetbrains.annotations.Nullable

import static java.util.Objects.requireNonNull

@EqualsAndHashCode(includeFields = true)
final class EmbeddablePartsMap {

    @Delegate
    private final Map<String, EmbeddablePart> partsMap = [:]

    EmbeddablePartsMap(
            RenderContext context,
            Closure<Void> onDiagnostics,
            @Nullable Text text = null
    ) {
        requireNonNull(context)
        requireNonNull(onDiagnostics)
        context.parts.each {
            this.put(it.path, new EmbeddablePart(it, context, onDiagnostics, text))
        }
    }

    @Override
    String toString() {
        "EmbeddablePartsMap(partsMap: ${ this.partsMap })"
    }

}
