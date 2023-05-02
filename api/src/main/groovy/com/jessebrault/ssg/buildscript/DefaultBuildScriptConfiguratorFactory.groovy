package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.html.PageToHtmlTaskFactory
import com.jessebrault.ssg.html.TextToHtmlSpec
import com.jessebrault.ssg.html.TextToHtmlTaskFactory
import com.jessebrault.ssg.page.PageTypes
import com.jessebrault.ssg.page.PagesProviders
import com.jessebrault.ssg.part.PartTypes
import com.jessebrault.ssg.part.PartsProviders
import com.jessebrault.ssg.provider.CollectionProviders
import com.jessebrault.ssg.template.TemplateTypes
import com.jessebrault.ssg.template.TemplatesProviders
import com.jessebrault.ssg.text.TextTypes
import com.jessebrault.ssg.text.TextsProviders
import com.jessebrault.ssg.util.ExtensionUtil
import com.jessebrault.ssg.util.Result
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

import java.util.function.Consumer

@TupleConstructor(includeFields = true, defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode(includeFields = true)
final class DefaultBuildScriptConfiguratorFactory implements BuildScriptConfiguratorFactory {

    private final File baseDir

    @Override
    Consumer<BuildScriptBase> get() {
        return {
            it.allBuilds {
                types {
                    textTypes << TextTypes.MARKDOWN
                    pageTypes << PageTypes.GSP
                    templateTypes << TemplateTypes.GSP
                    partTypes << PartTypes.GSP
                }

                providers { types ->
                    texts(TextsProviders.from(new File(this.baseDir, 'texts'), types.textTypes))
                    pages(PagesProviders.from(new File(this.baseDir, 'pages'), types.pageTypes))
                    templates(TemplatesProviders.from(new File(this.baseDir, 'templates'), types.templateTypes))
                    parts(PartsProviders.of(new File(this.baseDir, 'parts'), types.partTypes))
                }

                taskFactories { sourceProviders ->
                    register('textToHtml', TextToHtmlTaskFactory::new) {
                        it.specProvider += CollectionProviders.fromSupplier {
                            def templates = sourceProviders.templatesProvider.provide()
                            sourceProviders.textsProvider.provide().collect {
                                def frontMatterResult = it.type.frontMatterGetter.get(it)
                                if (frontMatterResult.hasDiagnostics()) {
                                    return Result.ofDiagnostics(frontMatterResult.diagnostics)
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
                        it.allTextsProvider += sourceProviders.textsProvider
                        it.allPartsProvider += sourceProviders.partsProvider
                    }

                    register('pageToHtml', PageToHtmlTaskFactory::new) {
                        it.pagesProvider += sourceProviders.pagesProvider
                        it.allTextsProvider += sourceProviders.textsProvider
                        it.allPartsProvider += sourceProviders.partsProvider
                    }
                }
            }

            it.build('default') {
                outputDir = new File(this.baseDir, 'build')
            }
        }
    }

}
