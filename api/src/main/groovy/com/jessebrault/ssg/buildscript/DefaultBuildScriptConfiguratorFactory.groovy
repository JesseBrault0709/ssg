package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.html.PageToHtmlTaskFactory
import com.jessebrault.ssg.html.TextToHtmlSpec
import com.jessebrault.ssg.html.TextToHtmlTaskFactory
import com.jessebrault.ssg.page.PageTypes
import com.jessebrault.ssg.page.PagesProviders
import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.part.PartTypes
import com.jessebrault.ssg.provider.CollectionProviders
import com.jessebrault.ssg.template.TemplatesProviders
import com.jessebrault.ssg.text.TextsProviders
import com.jessebrault.ssg.template.Template
import com.jessebrault.ssg.template.TemplateTypes
import com.jessebrault.ssg.text.TextTypes
import com.jessebrault.ssg.util.ExtensionUtil
import com.jessebrault.ssg.util.PathUtil
import com.jessebrault.ssg.util.Result
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.function.Consumer

final class DefaultBuildScriptConfiguratorFactory implements BuildScriptConfiguratorFactory {

    private static final Logger logger = LoggerFactory.getLogger(DefaultBuildScriptConfiguratorFactory)

    @Override
    Consumer<BuildScriptBase> get() {
        return {
            it.allBuilds {
                types {
                    textTypes << TextTypes.MARKDOWN
                    pageTypes << PageTypes.GSP
                    templateTypes << TemplateTypes.GSP
                    partTypes << PartTypes.GSP

                    //noinspection GroovyUnnecessaryReturn
                    return
                }

                providers { types ->
                    texts(TextsProviders.from(new File('texts'), types.textTypes))
                    pages(PagesProviders.from(new File('pages'), types.pageTypes))
                    templates(TemplatesProviders.from(new File('templates'), types.templateTypes))

                    parts(CollectionProviders.from(new File('parts')) { File file ->
                        def extension = ExtensionUtil.getExtension(file.path)
                        def partType = types.partTypes.find { it.ids.contains(extension) }
                        if (!partType) {
                            logger.warn('there is no PartType for file {}; skipping', file)
                            null
                        } else {
                            new Part(PathUtil.relative('parts', file.path), partType, file.getText())
                        }
                    })
                }

                taskFactories { sp ->
                    register('textToHtml', TextToHtmlTaskFactory::new) {
                        it.specProvider += CollectionProviders.from {
                            def templates = sp.templatesProvider.provide()
                            sp.textsProvider.provide().collect {
                                def frontMatterResult = it.type.frontMatterGetter.get(it)
                                if (frontMatterResult.hasDiagnostics()) {
                                    return Result.ofDiagnostics(frontMatterResult.diagnostics)
                                }
                                def templateValue = frontMatterResult.get().get('template')
                                if (templateValue) {
                                    def template = templates.find { it.path == templateValue }
                                    return Result.of(new TextToHtmlSpec(it, template, it.path))
                                } else {
                                    return null
                                }
                            }
                        }
                        it.allTextsProvider += sp.textsProvider
                        it.allPartsProvider += sp.partsProvider

                        //noinspection GroovyUnnecessaryReturn
                        return
                    }

                    register('pageToHtml', PageToHtmlTaskFactory::new) {
                        it.pagesProvider += sp.pagesProvider
                        it.allTextsProvider += sp.textsProvider
                        it.allPartsProvider += sp.partsProvider

                        //noinspection GroovyUnnecessaryReturn
                        return
                    }
                }
            }
        }
    }

}
