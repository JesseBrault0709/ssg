package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.html.PageToHtmlSpecProviders
import com.jessebrault.ssg.html.PageToHtmlTaskFactory
import com.jessebrault.ssg.html.TextToHtmlSpecProviders
import com.jessebrault.ssg.html.TextToHtmlTaskFactory
import com.jessebrault.ssg.page.PageTypes
import com.jessebrault.ssg.page.PagesProviders
import com.jessebrault.ssg.part.PartTypes
import com.jessebrault.ssg.part.PartsProviders
import com.jessebrault.ssg.template.TemplateTypes
import com.jessebrault.ssg.template.TemplatesProviders
import com.jessebrault.ssg.text.TextTypes
import com.jessebrault.ssg.text.TextsProviders
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

import java.util.function.Consumer
import java.util.function.Supplier

@TupleConstructor(includeFields = true, defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode(includeFields = true)
final class DefaultBuildScriptConfiguratorFactory implements BuildScriptConfiguratorFactory {

    private final File baseDir
    private final Supplier<ClassLoader> classLoaderSupplier

    @Override
    Consumer<BuildScriptBase> get() {
        return {
            it.build(name: 'default') {
                outputDirFunction = { Build build ->
                    new OutputDir(new File(this.baseDir, build.name == 'default' ? 'build' : build.name))
                }

                types {
                    textTypes << TextTypes.MARKDOWN
                    pageTypes << PageTypes.getGsp(['.gsp', '.ssg.gst'], this.classLoaderSupplier.get())
                    templateTypes << TemplateTypes.getGsp(['.gsp', '.ssg.gst'], this.classLoaderSupplier.get())
                    partTypes << PartTypes.getGsp(['.gsp', '.ssg.gst'], this.classLoaderSupplier.get())
                }

                sources { base, types ->
                    texts TextsProviders.from(new File(this.baseDir, 'texts'), types.textTypes)
                    pages PagesProviders.from(new File(this.baseDir, 'pages'), types.pageTypes)
                    templates TemplatesProviders.from(new File(this.baseDir, 'templates'), types.templateTypes)
                    parts PartsProviders.from(new File(this.baseDir, 'parts'), types.partTypes)
                }

                taskFactories { base, sources ->
                    register('textToHtml', TextToHtmlTaskFactory::new) {
                        it.specsProvider += TextToHtmlSpecProviders.from(sources)
                        it.allTextsProvider += sources.textsProvider
                        it.allPartsProvider += sources.partsProvider
                        it.allModelsProvider += sources.modelsProvider
                    }

                    register('pageToHtml', PageToHtmlTaskFactory::new) {
                        it.specsProvider += PageToHtmlSpecProviders.from(sources.pagesProvider)
                        it.allTextsProvider += sources.textsProvider
                        it.allPartsProvider += sources.partsProvider
                        it.allModelsProvider += sources.modelsProvider
                    }
                }
            }
        }
    }

}
