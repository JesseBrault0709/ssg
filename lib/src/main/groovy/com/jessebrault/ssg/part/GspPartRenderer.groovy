package com.jessebrault.ssg.part

import com.jessebrault.ssg.Diagnostic
import com.jessebrault.ssg.SiteSpec
import com.jessebrault.ssg.tagbuilder.DynamicTagBuilder
import com.jessebrault.ssg.text.EmbeddableText
import com.jessebrault.ssg.url.PathBasedUrlBuilder
import groovy.text.GStringTemplateEngine
import groovy.text.TemplateEngine
import groovy.transform.EqualsAndHashCode
import org.jetbrains.annotations.Nullable

@EqualsAndHashCode
class GspPartRenderer implements PartRenderer {

    private static final TemplateEngine engine = new GStringTemplateEngine()

    @Override
    Tuple2<Collection<Diagnostic>, String> render(
            Part part,
            Map binding,
            SiteSpec siteSpec,
            Map globals,
            @Nullable EmbeddableText text = null,
            Collection<Part> allParts,
            String path,
            String targetPath
    ) {
        Objects.requireNonNull(part)
        Objects.requireNonNull(binding)
        Objects.requireNonNull(siteSpec)
        Objects.requireNonNull(globals)
        Objects.requireNonNull(allParts)
        Objects.requireNonNull(path)
        Objects.requireNonNull(targetPath)
        def embeddedPartDiagnostics = []
        try {
            def result = engine.createTemplate(part.text).make([
                    binding: binding,
                    globals: globals,
                    parts: new EmbeddablePartsMap(
                            allParts,
                            siteSpec,
                            globals,
                            embeddedPartDiagnostics.&addAll,
                            text,
                            path,
                            targetPath
                    ),
                    path: path,
                    siteSpec: siteSpec,
                    tagBuilder: new DynamicTagBuilder(),
                    targetPath: targetPath,
                    text: text,
                    urlBuilder: new PathBasedUrlBuilder(targetPath, siteSpec.baseUrl)
            ])
            new Tuple2<>([], result.toString())
        } catch (Exception e) {
            def diagnostic = new Diagnostic(
                    "An exception occurred while rendering part ${ part.path }:\n${ e }",
                    e
            )
            new Tuple2<>(
                    [diagnostic, *embeddedPartDiagnostics],
                    ''
            )
        }
    }

    @Override
    String toString() {
        "GspPartRenderer()"
    }

}
