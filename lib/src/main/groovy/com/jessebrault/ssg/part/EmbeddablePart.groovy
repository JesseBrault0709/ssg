package com.jessebrault.ssg.part

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(includeFields = true, defaults = false)
@NullCheck
@EqualsAndHashCode(includeFields = true)
class EmbeddablePart {

    private final Part part
    private final Map globals

    String render(Map binding = [:]) {
        part.type.renderer.render(this.part.text, binding, this.globals)
    }

    @Override
    String toString() {
        "EmbeddablePart(part: ${ this.part }, globals: ${ this.globals })"
    }

}
