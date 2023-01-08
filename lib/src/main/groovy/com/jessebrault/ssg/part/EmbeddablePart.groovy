package com.jessebrault.ssg.part

import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(includeFields = true, defaults = false)
@NullCheck
 class EmbeddablePart {

    private final Part part
    private final Map globals

    String render(Map binding = [:]) {
        part.type.renderer.render(this.part.text, binding, this.globals)
    }

}
