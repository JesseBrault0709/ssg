package com.jessebrault.ssg

import com.jessebrault.ssg.buildscript.BuildScriptGetter
import com.jessebrault.ssg.buildscript.BuildScriptToBuildSpecConverter
import com.jessebrault.ssg.buildscript.BuildSpec
import com.jessebrault.ssg.buildscript.delegates.BuildDelegate
import com.jessebrault.ssg.di.*
import com.jessebrault.ssg.page.DefaultWvcPage
import com.jessebrault.ssg.page.Page
import com.jessebrault.ssg.page.PageFactory
import com.jessebrault.ssg.page.PageSpec
import com.jessebrault.ssg.text.Text
import com.jessebrault.ssg.util.Diagnostic
import com.jessebrault.ssg.view.PageView
import com.jessebrault.ssg.view.SkipTemplate
import com.jessebrault.ssg.view.WvcCompiler
import com.jessebrault.ssg.view.WvcPageView
import groovy.transform.TupleConstructor
import groowt.util.di.ObjectFactory
import groowt.util.di.RegistryObjectFactory
import groowt.util.fp.option.Option
import groowt.view.component.compiler.DefaultComponentTemplateCompilerConfiguration
import groowt.view.component.compiler.SimpleComponentTemplateClassFactory
import groowt.view.component.factory.ComponentFactories
import groowt.view.component.web.DefaultWebViewComponentContext
import groowt.view.component.web.WebViewComponent
import groowt.view.component.web.WebViewComponentContext
import groowt.view.component.web.WebViewComponentScope
import io.github.classgraph.ClassGraph
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.file.Files
import java.nio.file.Path

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

    protected WvcCompiler getWvcCompiler() {
        def wvcCompilerConfiguration = new DefaultComponentTemplateCompilerConfiguration()
        wvcCompilerConfiguration.groovyClassLoader = this.groovyClassLoader
        def templateClassFactory = new SimpleComponentTemplateClassFactory(this.groovyClassLoader)
        new WvcCompiler(wvcCompilerConfiguration, templateClassFactory)
    }

    protected WebViewComponentContext makeContext(
            Set<Class<? extends WebViewComponent>> allWvc,
            RegistryObjectFactory objectFactory,
            WvcPageView pageView
    ) {
        new DefaultWebViewComponentContext().tap {
            configureRootScope(WebViewComponentScope) {
                // custom components
                allWvc.each { wvcClass ->
                    //noinspection GroovyAssignabilityCheck
                    add(wvcClass, ComponentFactories.ofClosureClassType(wvcClass) { Map attr, Object[] args ->
                        WebViewComponent component
                        if (!attr.isEmpty() && args.length > 0) {
                            component = objectFactory.createInstance(wvcClass, attr, *args)
                        } else if (!attr.isEmpty()) {
                            component = objectFactory.createInstance(wvcClass, attr)
                        } else if (args.length > 0) {
                            component = objectFactory.createInstance(wvcClass, *args)
                        } else {
                            component = objectFactory.createInstance(wvcClass)
                        }
                        component.context = pageView.context
                        if (component.componentTemplate == null
                                && !wvcClass.isAnnotationPresent(SkipTemplate)) {
                            def compileResult = objectFactory.get(WvcCompiler).compileTemplate(
                                    wvcClass,
                                    wvcClass.simpleName + 'Template.wvc'
                            )
                            if (compileResult.isRight()) {
                                component.componentTemplate = compileResult.getRight()
                            } else {
                                def left = compileResult.getLeft()
                                throw new RuntimeException(left.message, left.exception)
                            }
                        }
                        return component
                    })
                }
            }
        }
    }

    protected Option<Diagnostic> handlePage(
            String buildScriptFqn,
            Page page,
            BuildSpec buildSpec,
            Set<Class<? extends WebViewComponent>> allWvc,
            ObjectFactory objectFactory
    ) {
        // instantiate PageView
        def pageViewResult = page.createView()
        if (pageViewResult.isLeft()) {
            return Option.lift(pageViewResult.getLeft())
        }
        PageView pageView = pageViewResult.getRight()

        // Prepare for rendering
        pageView.pageTitle = page.name
        pageView.url = buildSpec.baseUrl.get() + page.path

        if (pageView instanceof WvcPageView) {
            pageView.context = this.makeContext(allWvc, objectFactory, pageView)
        }

        // Render the page
        def sw = new StringWriter()
        try {
            pageView.renderTo(sw)
        } catch (Exception exception) {
            return Option.lift(new Diagnostic(
                    "There was an exception while rendering $page.name as $pageView.class.name",
                    exception
            ))
        }

        // Output the page if not dryRun
        if (!this.dryRun) {
            def outputDir = buildSpec.outputDir.get {
                new SsgException("The outputDir Property in $buildScriptFqn must be set.")
            }
            outputDir.mkdirs()

            def splitPathParts = page.path.split('/')
            def pathParts = page.path.endsWith('/')
                    ? splitPathParts + 'index'
                    : splitPathParts

            def path = Path.of(pathParts[0], pathParts.drop(1))

            def outputFile = new File(
                    outputDir,
                    path.toString() + page.fileExtension
            )
            outputFile.parentFile.mkdirs()
            try {
                outputFile.write(sw.toString())
            } catch (Exception exception) {
                return Option.lift(new Diagnostic(
                        "There was an exception while writing $page.name to $outputFile",
                        exception
                ))
            }
        }

        return Option.empty()
    }

    @Override
    Collection<Diagnostic> doBuild(
            File projectDir,
            String buildName,
            String buildScriptFqn,
            Map<String, String> buildScriptCliArgs
    ) {
        def wvcCompiler = this.getWvcCompiler()

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

        // configure objectFactory for Page/PageFactory instantiation
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
            bind(WvcCompiler, toSingleton(wvcCompiler))
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
                .addClassLoader(this.groovyClassLoader)
        basePackages.each { classgraph.acceptPackages(it) }

        def pages = [] as Set<Page>
        def allWvc = [] as Set<Class<? extends WebViewComponent>>

        try (def scanResult = classgraph.scan()) {
            // single pages
            def pageViewInfoList = scanResult.getClassesImplementing(PageView)
            pageViewInfoList.each { pageViewInfo ->
                def pageSpecInfo = pageViewInfo.getAnnotationInfo(PageSpec)
                if (pageSpecInfo != null) {
                    def pageSpec = (PageSpec) pageSpecInfo.loadClassAndInstantiate()
                    pages << new DefaultWvcPage(
                            name: pageSpec.name(),
                            path: pageSpec.path(),
                            fileExtension: pageSpec.fileExtension(),
                            viewType: (Class<? extends PageView>) pageViewInfo.loadClass(),
                            templateResource: !pageSpec.templateResource().empty
                                    ? pageSpec.templateResource()
                                    : pageViewInfo.simpleName + 'Template.wvc',
                            objectFactory: objectFactory,
                            wvcCompiler: wvcCompiler
                    )
                }
            }

            // page factories
            def pageFactoryInfoList = scanResult.getClassesImplementing(PageFactory)
            def pageFactoryTypes = pageFactoryInfoList.collect() { pageFactoryInfo ->
                (pageFactoryInfo.loadClass() as Class<? extends PageFactory>)
            }

            // instantiate page factory and create the pages
            pageFactoryTypes.each { pageFactoryType ->
                def pageFactory = objectFactory.createInstance(pageFactoryType)
                def created = pageFactory.create()
                pages.addAll(created)
            }

            // get all web view components
            def wvcInfoList = scanResult.getClassesImplementing(WebViewComponent)
            wvcInfoList.each {
                allWvc << it.loadClass(WebViewComponent)
            }
        }

        // Configure objectFactory for PageView instantiation with the Pages we found/created
        objectFactory.configureRegistry {
            // extensions
            addExtension(new PagesExtension().tap {
                allPages.addAll(pages)
            })
            addExtension(new SelfPageExtension())
        }

        def diagnostics = [] as Collection<Diagnostic>

        pages.each {
            def result = this.handlePage(buildScriptFqn, it, buildSpec, allWvc, objectFactory)
            if (result.isPresent()) {
                diagnostics << result.get()
            }
        }

        // return diagnostics
        diagnostics
    }

}
