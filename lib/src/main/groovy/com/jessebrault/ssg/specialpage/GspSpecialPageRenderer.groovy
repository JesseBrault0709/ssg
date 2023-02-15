package com.jessebrault.ssg.specialpage

import com.jessebrault.ssg.Diagnostic
import com.jessebrault.ssg.SiteSpec
import com.jessebrault.ssg.part.EmbeddablePartsMap
import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.tagbuilder.DynamicTagBuilder
import com.jessebrault.ssg.text.EmbeddableTextsCollection
import com.jessebrault.ssg.text.Text
import com.jessebrault.ssg.url.PathBasedUrlBuilder
import groovy.text.GStringTemplateEngine
import groovy.text.TemplateEngine
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck

@NullCheck
@EqualsAndHashCode
class GspSpecialPageRenderer implements SpecialPageRenderer {

    private static final TemplateEngine engine = new GStringTemplateEngine()

    @Override
    Tuple2<Collection<Diagnostic>, String> render(
            SpecialPage specialPage,
            Collection<Text> texts,
            Collection<Part> parts,
            SiteSpec siteSpec,
            Map globals,
            String targetPath
    ) {
        try {
            Collection<Diagnostic> diagnostics = []
            def result = engine.createTemplate(specialPage.text).make([
                    globals: globals,
                    parts: new EmbeddablePartsMap(
                            parts,
                            siteSpec,
                            globals,
                            diagnostics.&addAll,
                            specialPage.path,
                            targetPath
                    ),
                    path: specialPage.path,
                    tagBuilder: new DynamicTagBuilder(),
                    targetPath: targetPath,
                    texts: new EmbeddableTextsCollection(texts, globals, diagnostics.&addAll),
                    urlBuilder: new PathBasedUrlBuilder(targetPath, siteSpec.baseUrl)
            ])
            new Tuple2<>(diagnostics, result.toString())
        } catch (Exception e) {
            new Tuple2<>([new Diagnostic("An exception occurred while rendering specialPage ${ specialPage.path }:\n${ e }", e)], '')
        }
    }

    @Override
    String toString() {
        "GspSpecialPageRenderer()"
    }

}
