package com.jessebrault.ssg.dsl

import com.jessebrault.ssg.render.RenderContext
import com.jessebrault.ssg.text.Text
import com.jessebrault.ssg.util.Diagnostic
import groovy.transform.EqualsAndHashCode
import org.jetbrains.annotations.Nullable

import java.util.function.Consumer

import static java.util.Objects.requireNonNull

@EqualsAndHashCode(includeFields = true)
final class EmbeddablePartsMap {

    @Delegate
    private final Map<String, EmbeddablePart> partsMap = [:]

    EmbeddablePartsMap(
            RenderContext context,
            Consumer<Collection<Diagnostic>> diagnosticsConsumer,
            @Nullable Text text = null
    ) {
        requireNonNull(context)
        requireNonNull(diagnosticsConsumer)
        context.parts.each {
            this[it.path] = new EmbeddablePart(it, context, diagnosticsConsumer, text)
        }
    }

    @Override
    String toString() {
        "EmbeddablePartsMap(partsMap: ${ this.partsMap })"
    }

}
