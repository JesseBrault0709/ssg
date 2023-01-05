package com.jessebrault.ssg.part

import groovy.transform.TupleConstructor

class PartsMap {

    @TupleConstructor(includeFields = true, defaults = false)
    class RenderingPart {

        private final Part part

        String render(Map binding) {
            part.type.renderer.render(part.text, binding)
        }

    }

    private final Map<String, RenderingPart> partsMap = new LinkedHashMap<>()

    PartsMap(Collection<Part> parts) {
        Objects.requireNonNull(parts)
        parts.each {
            this.partsMap.put(it.name, new RenderingPart(it))
        }
    }

    RenderingPart get(String name) {
        this.partsMap.get(Objects.requireNonNull(name))
    }

    RenderingPart getAt(String name) {
        this.get(Objects.requireNonNull(name))
    }

}
