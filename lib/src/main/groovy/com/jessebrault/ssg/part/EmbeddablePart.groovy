package com.jessebrault.ssg.part

import groovy.transform.TupleConstructor

@TupleConstructor(includeFields = true, defaults = false)
 class EmbeddablePart {

    private final Part part

    String render(Map binding) {
        part.type.renderer.render(part.text, binding)
    }

}
