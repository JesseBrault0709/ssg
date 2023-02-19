package com.jessebrault.ssg.part


import com.jessebrault.ssg.renderer.RenderContext
import com.jessebrault.ssg.text.Text
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import org.jetbrains.annotations.Nullable

import static java.util.Objects.requireNonNull

@EqualsAndHashCode(includeFields = true)
class EmbeddablePart {

    private final Part part
    private final RenderContext context
    private final Closure onDiagnostics

    @Nullable
    private final Text text

    EmbeddablePart(
            Part part,
            RenderContext context,
            Closure onDiagnostics,
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
        if (result.v1.size() > 0) {
            this.onDiagnostics.call(result.v1)
            ''
        } else {
            result.v2
        }
    }

    @Override
    String toString() {
        "EmbeddablePart(part: ${ this.part })"
    }

}
