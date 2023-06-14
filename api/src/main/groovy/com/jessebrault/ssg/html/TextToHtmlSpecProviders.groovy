package com.jessebrault.ssg.html

import com.jessebrault.ssg.buildscript.SourceProviders
import com.jessebrault.ssg.provider.CollectionProvider
import com.jessebrault.ssg.provider.CollectionProviders
import com.jessebrault.ssg.task.TaskSpec
import com.jessebrault.ssg.template.Template
import com.jessebrault.ssg.text.Text
import com.jessebrault.ssg.util.ExtensionUtil
import com.jessebrault.ssg.util.Result

import java.util.function.Function

final class TextToHtmlSpecProviders {

    static CollectionProvider<Result<TextToHtmlSpec>> from(SourceProviders sources) {
        from(sources) { text ->
            return { TaskSpec taskSpec ->
                ExtensionUtil.stripExtension(text.path) + '.html'
            }
        }
    }

    static CollectionProvider<Result<TextToHtmlSpec>> from(
            SourceProviders sources,
            Function<Text, Function<TaskSpec, String>> toRelativeHtmlPath
    ) {
        from(sources.textsProvider, sources.templatesProvider, toRelativeHtmlPath)
    }

    static CollectionProvider<Result<TextToHtmlSpec>> from(
            CollectionProvider<Text> textsProvider,
            CollectionProvider<Template> templatesProvider,
            Function<Text, Function<TaskSpec, String>> toRelativeHtmlPath
    ) {
        CollectionProviders.fromSupplier {
            def templates = templatesProvider.provide()
            textsProvider.provide().findResults {
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
                            toRelativeHtmlPath.apply(it)
                    ))
                } else {
                    return null
                }
            }
        }
    }

    private TextToHtmlSpecProviders() {}

}
