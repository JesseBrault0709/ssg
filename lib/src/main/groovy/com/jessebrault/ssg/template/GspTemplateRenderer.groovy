package com.jessebrault.ssg.template

import com.jessebrault.ssg.Diagnostic
import com.jessebrault.ssg.SiteSpec
import com.jessebrault.ssg.part.EmbeddablePartsMap
import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.tagbuilder.DynamicTagBuilder
import com.jessebrault.ssg.text.EmbeddableText
import com.jessebrault.ssg.text.FrontMatter
import com.jessebrault.ssg.text.Text
import com.jessebrault.ssg.url.PathBasedUrlBuilder
import groovy.text.GStringTemplateEngine
import groovy.text.TemplateEngine
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import org.slf4j.LoggerFactory

@NullCheck
@EqualsAndHashCode
class GspTemplateRenderer implements TemplateRenderer {

    private static final TemplateEngine engine = new GStringTemplateEngine()

    @Override
    Tuple2<Collection<Diagnostic>, String> render(
            Template template,
            @Deprecated
            FrontMatter frontMatter,
            Text text,
            Collection<Part> parts,
            SiteSpec siteSpec,
            Map globals,
            String targetPath
    ) {
        try {
            Collection<Diagnostic> diagnostics = []
            def onDiagnostics = { Collection<Diagnostic> partDiagnostics ->
                diagnostics.addAll(partDiagnostics)
            }
            def embeddableText = new EmbeddableText(text, globals, onDiagnostics)
            def result = engine.createTemplate(template.text).make([
                    frontMatter: frontMatter,
                    globals: globals,
                    logger: LoggerFactory.getLogger("Template(${ template.path })"),
                    parts: new EmbeddablePartsMap(parts, siteSpec, globals, onDiagnostics, embeddableText, text.path, targetPath),
                    path: text.path,
                    siteSpec: siteSpec,
                    tagBuilder: new DynamicTagBuilder(),
                    targetPath: targetPath,
                    text: embeddableText,
                    urlBuilder: new PathBasedUrlBuilder(targetPath, siteSpec.baseUrl)
            ])
            new Tuple2<>(diagnostics, result.toString())
        } catch (Exception e) {
            new Tuple2<>([new Diagnostic("An exception occurred while rendering Template ${ template.path }:\n${ e }", e)], '')
        }
    }

    @Override
    String toString() {
        "GspTemplateRenderer()"
    }

}
