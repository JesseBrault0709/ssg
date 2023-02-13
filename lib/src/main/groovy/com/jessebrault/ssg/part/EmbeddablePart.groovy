package com.jessebrault.ssg.part

import com.jessebrault.ssg.text.EmbeddableText
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor
import org.jetbrains.annotations.Nullable

@TupleConstructor(includeFields = true, defaults = false)
@NullCheck
@EqualsAndHashCode(includeFields = true)
class EmbeddablePart {

    private final Part part
    private final Map globals
    private final Closure onDiagnostics

    @Nullable
    private final EmbeddableText text

    private final Collection<Part> allParts

    String render(Map binding = [:]) {
        def result = part.type.renderer.render(
                this.part,
                binding,
                this.globals,
                this.text,
                this.allParts
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
        "EmbeddablePart(part: ${ this.part }, globals: ${ this.globals })"
    }

}
