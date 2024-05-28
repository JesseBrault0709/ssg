package com.jessebrault.ssg

import com.jessebrault.ssg.buildscript.BuildScriptGetter
import com.jessebrault.ssg.buildscript.BuildScriptToBuildSpecConverter
import com.jessebrault.ssg.buildscript.BuildSpec
import com.jessebrault.ssg.buildscript.delegates.BuildDelegate
import com.jessebrault.ssg.di.*
import com.jessebrault.ssg.page.DefaultPage
import com.jessebrault.ssg.page.Page
import com.jessebrault.ssg.page.PageFactory
import com.jessebrault.ssg.page.PageSpec
import com.jessebrault.ssg.text.Text
import com.jessebrault.ssg.util.Diagnostic
import com.jessebrault.ssg.view.PageView
import com.jessebrault.ssg.view.WvcPageView
import groovy.transform.TupleConstructor
import groowt.util.di.RegistryObjectFactory
import groowt.view.component.web.DefaultWebViewComponentContext
import io.github.classgraph.ClassGraph
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.file.Files

import static groowt.util.di.BindingUtil.named
import static groowt.util.di.BindingUtil.toSingleton

@TupleConstructor(includeFields = true, defaults = false)
class DefaultStaticSiteGenerator implements StaticSiteGenerator {

    private static final Logger logger = LoggerFactory.getLogger(DefaultStaticSiteGenerator)

    protected final GroovyClassLoader groovyClassLoader
    protected final URL[] buildScriptBaseUrls
    protected final boolean dryRun

    protected Set<Text> getTexts(String buildScriptFqn, BuildSpec buildSpec) {
        def textConverters = buildSpec.textConverters.get {
            new SsgException("The textConverters Property in $buildScriptFqn must contain at least an empty Set.")
        }
        def textDirs = buildSpec.textsDirs.get {
            new SsgException("The textDirs Property in $buildScriptFqn must contain at least an empty Set.")
        }
        def texts = [] as Set<Text>
        textDirs.each { textDir ->
            if (textDir.exists()) {
                Files.walk(textDir.toPath()).each {
                    def asFile = it.toFile()
                    def lastDot = asFile.name.lastIndexOf('.')
                    if (lastDot != -1) {
                        def extension = asFile.name.substring(lastDot)
                        def converter = textConverters.find {
                            it.handledExtensions.contains(extension)
                        }
                        texts << converter.convert(textDir, asFile)
                    }
                }
            }
        }
        texts
    }

    @Override
    Collection<Diagnostic> doBuild(
            File projectDir,
            String buildName,
            String buildScriptFqn,
            Map<String, String> buildScriptCliArgs
    ) {
        // run build script(s) and get buildSpec
        def buildScriptGetter = new BuildScriptGetter(
                this.groovyClassLoader,
                this.buildScriptBaseUrls,
                buildScriptCliArgs,
                projectDir
        )
        def buildScriptToBuildSpecConverter = new BuildScriptToBuildSpecConverter(
                buildScriptGetter, { String name -> BuildDelegate.withDefaults(name, projectDir) }
        )
        def buildSpec = buildScriptToBuildSpecConverter.convert(buildScriptFqn)

        // prepare objectFactory
        def objectFactoryBuilder = buildSpec.objectFactoryBuilder.get {
            new SsgException(
                    "the objectFactoryBuilder Property in $buildScriptFqn " +
                            "must contain a RegistryObjectFactory.Builder instance."
            )
        }

        // configure for Page instantiation
        objectFactoryBuilder.configureRegistry {
            // extensions
            addExtension(new TextsExtension().tap {
                allTexts.addAll(this.getTexts(buildScriptFqn, buildSpec))
            })
            addExtension(new ModelsExtension().tap {
                allModels.addAll(buildSpec.models.get {
                    new SsgException("The models Property in $buildScriptFqn  must contain at least an empty Set.")
                })
            })
            addExtension(new GlobalsExtension().tap {
                globals.putAll(buildSpec.globals.get {
                    new SsgException("The globals Property in $buildScriptFqn must contain at least an empty Map.")
                })
            })

            // bindings
            bind(named('buildName', String), toSingleton(buildSpec.name))
            bind(named('siteName', String), toSingleton(buildSpec.siteName.get {
                new SsgException("The siteName Property in $buildScriptFqn must be set.")
            }))
            bind(named('baseUrl', String), toSingleton(buildSpec.baseUrl.get {
                new SsgException("The baseUrl Property in $buildScriptFqn must be set.")
            }))
        }

        // get the objectFactory
        def objectFactory = objectFactoryBuilder.build()
        objectFactory.configureRegistry {
            bind(RegistryObjectFactory, toSingleton(objectFactory))
        }

        // prepare for basePackages scan for Pages and PageFactories
        def basePackages = buildSpec.basePackages.get {
            new SsgException("The basePackages Property in $buildScriptFqn must contain at least an empty Set.")
        }
        def classgraph = new ClassGraph()
                .enableAnnotationInfo()
                .addClassLoader(groovyClassLoader)
        basePackages.each { classgraph.acceptPackages(it) }

        def pages = [] as Set<Page>

        try (def scanResult = classgraph.scan()) {
            // single pages
            def pageViewInfoList = scanResult.getClassesImplementing(PageView)
            pageViewInfoList.each { pageViewInfo ->
                def pageSpecInfo = pageViewInfo.getAnnotationInfo(PageSpec)
                if (pageSpecInfo != null) {
                    def pageSpec = (PageSpec) pageSpecInfo.loadClassAndInstantiate()
                    pages << new DefaultPage(
                            pageSpec.name(),
                            pageSpec.path(),
                            pageSpec.fileExtension(),
                            (Class<? extends PageView>) pageViewInfo.loadClass()
                    )
                }
            }

            // page factories
            def pageFactoryTypes = [] as Set<Class<? extends PageFactory>>

            def pageFactoryInfoList = scanResult.getClassesImplementing(PageFactory)
            pageFactoryInfoList.each { pageFactoryInfo ->
                pageFactoryTypes << (pageFactoryInfo.loadClass() as Class<? extends PageFactory>)
            }

            // instantiate page factory and create the pages
            pageFactoryTypes.each { pageFactoryType ->
                def pageFactory = objectFactory.createInstance(pageFactoryType)
                pages.addAll(pageFactory.create())
            }
        }

        // Configure for PageView instantiation
        objectFactoryBuilder.configureRegistry {
            // extensions
            addExtension(new PagesExtension().tap {
                allPages.addAll(pages)
            })
            addExtension(new SelfPageExtension())
        }

        def diagnostics = [] as Collection<Diagnostic>

        pages.each {
            // instantiate PageView
            PageView pageView
            try {
                objectFactory.registry.getExtension(SelfPageExtension).currentPage = it
                pageView = objectFactory.createInstance(it.viewType)
            } catch (Exception exception) {
                diagnostics << new Diagnostic(
                        "There was an exception while constructing $it.viewType.name for $it.name",
                        exception
                )
                return
            }

            // Prepare for rendering
            pageView.pageTitle = it.name
            pageView.url = buildSpec.baseUrl.get() + it.path
            if (pageView instanceof WvcPageView) {
                pageView.context = new DefaultWebViewComponentContext()
            }

            // Render the page
            def sw = new StringWriter()
            try {
                pageView.renderTo(sw)
            } catch (Exception exception) {
                diagnostics << new Diagnostic(
                        "There was an exception while rendering $it.name as $pageView.class.name",
                        exception
                )
                return
            }

            // Output the page if not dryRun
            if (!this.dryRun) {
                def outputDir = buildSpec.outputDir.get {
                    new SsgException("The outputDir Property in $buildScriptFqn must be set.")
                }
                outputDir.mkdirs()

                def outputFile = new File(
                        outputDir,
                        it.path.replace('/', File.separator) + it.fileExtension
                )
                try {
                    outputFile.write(sw.toString())
                } catch (Exception exception) {
                    diagnostics << new Diagnostic(
                            "There was an exception while writing $it.name to $outputFile",
                            exception
                    )
                }
            }
        }

        // return diagnostics
        diagnostics
    }

}
