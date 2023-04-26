package com.jessebrault.ssg.dsl

import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.render.RenderContext
import com.jessebrault.ssg.text.Text
import com.jessebrault.ssg.util.Diagnostic
import groovy.transform.EqualsAndHashCode
import org.jetbrains.annotations.Nullable

import java.util.function.Consumer

import static java.util.Objects.requireNonNull

@EqualsAndHashCode(includeFields = true)
final class EmbeddablePart {

    private final Part part
    private final RenderContext context
    private final Consumer<Collection<Diagnostic>> diagnosticsConsumer

    @Nullable
    private final Text text

    EmbeddablePart(
            Part part,
            RenderContext context,
            Consumer<Collection<Diagnostic>> diagnosticsConsumer,
            @Nullable Text text
    ) {
        this.part = requireNonNull(part)
        this.context = requireNonNull(context)
        this.diagnosticsConsumer = requireNonNull(diagnosticsConsumer)
        this.text = text
    }

    String render(Map binding = [:]) {
        def result = this.part.type.renderer.render(
                this.part,
                binding,
                this.context,
                this.text
        )
        if (result.hasDiagnostics()) {
            this.diagnosticsConsumer.accept(result.diagnostics)
            ''
        } else {
            result.get()
        }
    }

    @Override
    String toString() {
        "EmbeddablePart(part: ${ this.part })"
    }

}
