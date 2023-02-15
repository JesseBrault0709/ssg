package com.jessebrault.ssg.part

import com.jessebrault.ssg.SiteSpec
import com.jessebrault.ssg.text.EmbeddableText
import groovy.transform.EqualsAndHashCode
import org.jetbrains.annotations.Nullable

@EqualsAndHashCode(includeFields = true)
class EmbeddablePartsMap {

    @Delegate
    private final Map<String, EmbeddablePart> partsMap = [:]

    EmbeddablePartsMap(
            Collection<Part> parts,
            SiteSpec siteSpec,
            Map globals,
            Closure onDiagnostics,
            @Nullable EmbeddableText text = null,
            String path,
            String targetPath
    ) {
        Objects.requireNonNull(parts)
        Objects.requireNonNull(siteSpec)
        Objects.requireNonNull(globals)
        Objects.requireNonNull(onDiagnostics)
        Objects.requireNonNull(path)
        Objects.requireNonNull(targetPath)
        parts.each {
            this.put(it.path, new EmbeddablePart(it, siteSpec, globals, onDiagnostics, text, parts, path, targetPath))
        }
    }

    @Override
    String toString() {
        "EmbeddablePartsMap(partsMap: ${ this.partsMap })"
    }

}
