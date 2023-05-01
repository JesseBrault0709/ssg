import com.jessebrault.ssg.buildscript.Build
import com.jessebrault.ssg.buildscript.BuildScriptBase
import com.jessebrault.ssg.buildscript.OutputDir
import com.jessebrault.ssg.html.TextToHtmlSpec
import com.jessebrault.ssg.html.TextToHtmlTaskFactory
import com.jessebrault.ssg.provider.CollectionProviders
import com.jessebrault.ssg.template.TemplateTypes
import com.jessebrault.ssg.template.TemplatesProviders
import com.jessebrault.ssg.text.TextTypes
import com.jessebrault.ssg.text.TextsProviders
import com.jessebrault.ssg.util.ExtensionUtil
import com.jessebrault.ssg.util.Result
import groovy.transform.BaseScript

@BaseScript
BuildScriptBase base

build('test') {
    outputDirFunction = { Build b -> new OutputDir(new File(args.sourceDir, 'build')) }

    types {
        textTypes << TextTypes.MARKDOWN
        templateTypes << TemplateTypes.GSP
    }
    providers { types ->
        texts TextsProviders.from(new File(args.sourceDir, 'texts'), types.textTypes)
        templates TemplatesProviders.from(new File(args.sourceDir, 'templates'), types.templateTypes)
    }
    taskFactories { sources ->
        register('textToHtml', TextToHtmlTaskFactory::new) {
            it.specProvider += CollectionProviders.fromSupplier {
                def templates = sources.templatesProvider.provide()
                return sources.textsProvider.provide().collect {
                    def frontMatterResult = it.type.frontMatterGetter.get(it)
                    if (frontMatterResult.hasDiagnostics()) {
                        return Result.ofDiagnostics(frontMatterResult.diagnostics)
                    }
                    def desiredTemplate = frontMatterResult.get().get('template')
                    def template = templates.find { it.path == desiredTemplate }
                    if (template == null) {
                        throw new IllegalArgumentException('template is null')
                    }
                    return Result.of(new TextToHtmlSpec(it, template, ExtensionUtil.stripExtension(it.path) + '.html'))
                }
            }
        }
    }
}
