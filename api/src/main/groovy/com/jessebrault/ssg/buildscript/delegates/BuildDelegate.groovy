package com.jessebrault.ssg.buildscript.delegates

import com.jessebrault.ssg.model.Model
import com.jessebrault.ssg.model.Models
import com.jessebrault.ssg.text.MarkdownTextConverter
import com.jessebrault.ssg.text.TextConverter
import com.jessebrault.ssg.util.PathUtil
import groowt.util.di.DefaultRegistryObjectFactory
import groowt.util.di.RegistryObjectFactory
import groowt.util.fp.property.DefaultProperty
import groowt.util.fp.property.Property
import groowt.util.fp.provider.DefaultProvider
import groowt.util.fp.provider.NamedProvider
import groowt.util.fp.provider.Provider

import java.nio.file.Path
import java.util.function.Supplier

final class BuildDelegate {

    static BuildDelegate withDefaults(String buildName, File projectDir) {
        new BuildDelegate(projectDir).tap {
            basePackages.convention = [] as Set<String>
            outputDir.convention = PathUtil.resolve(projectDir, Path.of('dist', buildName.split(/\\./)))
            globals.convention = [:]
            models.convention = [] as Set<Model>
            textsDirs.convention = [new File(projectDir, 'texts')] as Set<File>
            textConverters.convention = [new MarkdownTextConverter()] as Set<TextConverter>
            objectFactoryBuilder.convention = DefaultRegistryObjectFactory.Builder.withDefaults()
        }
    }

    final File projectDir

    final Property<Set<String>> basePackages = DefaultProperty.<Set<String>>empty(Set)
    final Property<String> siteName = DefaultProperty.empty(String)
    final Property<String> baseUrl = DefaultProperty.empty(String)
    final Property<File> outputDir = DefaultProperty.empty(File)
    final Property<Map<String, Object>> globals = DefaultProperty.<Map<String, Object>>empty(Map)
    final Property<Set<Model>> models = DefaultProperty.<Set<Model>>empty(Set)
    final Property<Set<File>> textsDirs = DefaultProperty.<Set<File>>empty(Set)
    final Property<Set<TextConverter>> textConverters = DefaultProperty.<Set<TextConverter>>empty(Set)
    final Property<RegistryObjectFactory.Builder> objectFactoryBuilder =
            DefaultProperty.empty(RegistryObjectFactory.Builder)

    private BuildDelegate(File projectDir) {
        this.projectDir = projectDir
    }

    /* TODO: add friendly DSL methods for setting all properties */

    void basePackage(String toAdd) {
        this.basePackages.configure { it.add(toAdd) }
    }

    void basePackages(String... toAdd) {
        toAdd.each { this.basePackage(it) }
    }

    void siteName(String siteName) {
        this.siteName.set(siteName)
    }

    void siteName(Provider<String> siteNameProvider) {
        this.siteName.set(siteNameProvider)
    }

    void baseUrl(String baseUrl) {
        this.baseUrl.set(baseUrl)
    }

    void baseUrl(Provider<String> baseUrlProvider) {
        this.baseUrl.set(baseUrlProvider)
    }

    void outputDir(File outputDir) {
        this.outputDir.set(outputDir)
    }

    void outputDir(Provider<File> outputDirProvider) {
        this.outputDir.set(outputDirProvider)
    }

    void globals(@DelegatesTo(value = GlobalsDelegate, strategy = Closure.DELEGATE_FIRST) Closure globalsClosure) {
        def globalsDelegate = new GlobalsDelegate()
        globalsClosure.delegate = globalsDelegate
        globalsClosure.resolveStrategy = Closure.DELEGATE_FIRST
        globalsClosure()
        this.globals.set this.globals.get() + globalsDelegate
    }

    void model(String name, Object obj) {
        this.models.configure {it.add(Models.of(name, obj)) }
    }

    void model(Model model) {
        this.models.configure { it.add(model) }
    }

    void model(String name, Provider tProvider) {
        this.models.configure { it.add(Models.ofProvider(name, tProvider)) }
    }

    <T> void model(String name, Class<T> type, Supplier<? extends T> tSupplier) {
        this.models.configure { it.add(Models.ofSupplier(name, type, tSupplier)) }
    }

    void model(NamedProvider namedProvider) {
        this.models.configure { it.add(Models.ofNamedProvider(namedProvider)) }
    }

    void textsDir(File textsDir) {
        this.textsDirs.configure { it.add(textsDir) }
    }

    void textsDirs(File... textsDirs) {
        textsDirs.each { this.textsDir(it) }
    }

    void textConverter(TextConverter textConverter) {
        this.textConverters.configure { it.add(textConverter) }
    }

    void textConverters(TextConverter... textConverters) {
        textConverters.each { this.textConverter(it) }
    }

    void objectFactoryBuilder(RegistryObjectFactory.Builder builder) {
        this.objectFactoryBuilder.set(builder)
    }

}
