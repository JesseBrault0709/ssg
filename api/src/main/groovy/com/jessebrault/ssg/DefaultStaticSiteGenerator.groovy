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
import com.jessebrault.ssg.view.SkipTemplate
import com.jessebrault.ssg.view.WvcPageView
import groovy.transform.TupleConstructor
import groowt.util.di.RegistryObjectFactory
import groowt.util.fp.either.Either
import groowt.view.component.ComponentTemplate
import groowt.view.component.ViewComponent
import groowt.view.component.compiler.ComponentTemplateClassFactory
import groowt.view.component.compiler.ComponentTemplateCompilerConfiguration
import groowt.view.component.compiler.DefaultComponentTemplateCompilerConfiguration
import groowt.view.component.compiler.SimpleComponentTemplateClassFactory
import groowt.view.component.compiler.source.ComponentTemplateSource
import groowt.view.component.factory.ComponentFactories
import groowt.view.component.web.DefaultWebViewComponentContext
import groowt.view.component.web.WebViewComponent
import groowt.view.component.web.WebViewComponentScope
import groowt.view.component.web.compiler.DefaultWebViewComponentTemplateCompileUnit
import groowt.view.component.web.lib.Each
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

    protected Either<Diagnostic, ComponentTemplate> compileTemplate(
            Class<? extends ViewComponent> componentClass,
            String resourceName,
            ComponentTemplateCompilerConfiguration compilerConfiguration,
            ComponentTemplateClassFactory templateClassFactory
    ) {
        def templateUrl = componentClass.getResource(resourceName)
        if (templateUrl == null) {
            return Either.left(new Diagnostic(
                    "Could not find templateResource: $resourceName"
            ))
        }
        def source = ComponentTemplateSource.of(templateUrl)
        def compileUnit = new DefaultWebViewComponentTemplateCompileUnit(
                source.descriptiveName,
                componentClass,
                source,
                componentClass.packageName
        )
        def compileResult = compileUnit.compile(compilerConfiguration)
        def templateClass = templateClassFactory.getTemplateClass(compileResult)
        def componentTemplate = templateClass.getConstructor().newInstance()
        return Either.right(componentTemplate)
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
                    pages << new DefaultPage(
                            pageSpec.name(),
                            pageSpec.path(),
                            pageSpec.fileExtension(),
                            (Class<? extends PageView>) pageViewInfo.loadClass(),
                            !pageSpec.templateResource().empty
                                    ? pageSpec.templateResource()
                                    : pageViewInfo.simpleName + 'Template.wvc'
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

            // get all web view components
            def wvcInfoList = scanResult.getClassesImplementing(WebViewComponent)
            wvcInfoList.each {
                allWvc << it.loadClass(WebViewComponent)
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
        def wvcCompilerConfiguration = new DefaultComponentTemplateCompilerConfiguration()
        wvcCompilerConfiguration.groovyClassLoader = this.groovyClassLoader
        def componentTemplateClassFactory = new SimpleComponentTemplateClassFactory(this.groovyClassLoader)

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
                pageView.context = new DefaultWebViewComponentContext().tap {
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
                                    def compileResult = this.compileTemplate(
                                            wvcClass,
                                            wvcClass.simpleName + 'Template.wvc',
                                            wvcCompilerConfiguration,
                                            componentTemplateClassFactory
                                    )
                                    if (compileResult.isRight()) {
                                        component.componentTemplate = compileResult.getRight()
                                    } else {
                                        diagnostics << compileResult.getLeft()
                                    }
                                }
                                return component
                            })
                        }
                    }
                }

                if (pageView.componentTemplate == null) {
                    def compileResult = this.compileTemplate(
                            pageView.class,
                            it.templateResource,
                            wvcCompilerConfiguration,
                            componentTemplateClassFactory
                    )
                    if (compileResult.isRight()) {
                        pageView.componentTemplate = compileResult.getRight()
                    } else {
                        diagnostics << compileResult.getLeft()
                        return
                    }
                }
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
