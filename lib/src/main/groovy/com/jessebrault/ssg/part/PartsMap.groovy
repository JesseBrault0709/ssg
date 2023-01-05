package com.jessebrault.ssg.part

class PartsMap {

    private final Map<String, EmbeddablePart> partsMap = [:]

    PartsMap(Collection<Part> parts) {
        Objects.requireNonNull(parts)
        parts.each {
            this.partsMap.put(it.name, new EmbeddablePart(it))
        }
    }

    EmbeddablePart get(String name) {
        this.partsMap.get(Objects.requireNonNull(name))
    }

    EmbeddablePart getAt(String name) {
        this.get(Objects.requireNonNull(name))
    }

}
