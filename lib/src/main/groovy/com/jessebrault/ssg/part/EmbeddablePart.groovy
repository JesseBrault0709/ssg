package com.jessebrault.ssg.part

import groovy.transform.TupleConstructor

@TupleConstructor(includeFields = true, defaults = false)
 class EmbeddablePart {

    private final Part part
    private final Map globals

    String render(Map binding) {
        part.type.renderer.render(this.part.text, binding, this.globals)
    }

}
