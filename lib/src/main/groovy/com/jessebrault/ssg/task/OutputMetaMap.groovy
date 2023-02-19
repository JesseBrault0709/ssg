package com.jessebrault.ssg.task

final class OutputMetaMap {

    @Delegate
    private final Map<String, OutputMeta> map = [:]

    OutputMetaMap(Collection<OutputMeta> outputMetas) {
        outputMetas.each {
            this.put(it.sourcePath, it)
        }
    }

    @Override
    String toString() {
        "OutputMetaMap(map: ${ this.map })"
    }

}
