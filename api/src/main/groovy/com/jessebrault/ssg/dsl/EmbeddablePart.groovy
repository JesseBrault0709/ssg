package com.jessebrault.ssg.dsl

import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.render.RenderContext
import com.jessebrault.ssg.text.Text
import groovy.transform.EqualsAndHashCode
import org.jetbrains.annotations.Nullable

import static java.util.Objects.requireNonNull

@EqualsAndHashCode(includeFields = true)
final class EmbeddablePart {

    private final Part part
    private final RenderContext context
    private final Closure<Void> onDiagnostics

    @Nullable
    private final Text text

    EmbeddablePart(
            Part part,
            RenderContext context,
            Closure<Void> onDiagnostics,
            @Nullable Text text
    ) {
        this.part = requireNonNull(part)
        this.context = requireNonNull(context)
        this.onDiagnostics = requireNonNull(onDiagnostics)
        this.text = text
    }

    String render(Map binding = [:]) {
        def result = part.type.renderer.render(
                this.part,
                binding,
                this.context,
                this.text
        )
        if (result.hasDiagnostics()) {
            this.onDiagnostics.call(result.diagnostics)
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
