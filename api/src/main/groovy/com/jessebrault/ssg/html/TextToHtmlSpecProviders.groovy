package com.jessebrault.ssg.html

import com.jessebrault.ssg.buildscript.SourceProviders
import com.jessebrault.ssg.provider.CollectionProvider
import com.jessebrault.ssg.provider.CollectionProviders
import com.jessebrault.ssg.util.ExtensionUtil
import com.jessebrault.ssg.util.Result

final class TextToHtmlSpecProviders {

    static CollectionProvider<Result<TextToHtmlSpec>> from(SourceProviders sources) {
        CollectionProviders.fromSupplier {
            def templates = sources.templatesProvider.provide()
            sources.textsProvider.provide().findResults {
                def frontMatterResult = it.type.frontMatterGetter.get(it)
                if (frontMatterResult.hasDiagnostics()) {
                    return Result.ofDiagnostics(frontMatterResult.diagnostics) as Result<TextToHtmlSpec>
                }
                def templateValue = frontMatterResult.get().get('template')
                if (templateValue) {
                    def template = templates.find { it.path == templateValue }
                    return Result.of(new TextToHtmlSpec(
                            it,
                            template,
                            ExtensionUtil.stripExtension(it.path) + '.html'
                    ))
                } else {
                    return null
                }
            }
        }
    }

    private TextToHtmlSpecProviders() {}

}
