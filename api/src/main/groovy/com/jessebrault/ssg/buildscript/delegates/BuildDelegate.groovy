package com.jessebrault.ssg.buildscript.delegates

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groowt.util.di.DefaultRegistryObjectFactory
import groowt.util.di.RegistryObjectFactory
import groowt.util.fp.property.Property
import groowt.util.fp.provider.DefaultSetProvider
import groowt.util.fp.provider.Provider
import groowt.util.fp.provider.SetProvider

import java.util.function.Supplier

@NullCheck(includeGenerated = true)
@EqualsAndHashCode(includeFields = true)
final class BuildDelegate {

    static Supplier<BuildDelegate> withDefaults() {
        return {
            new BuildDelegate().tap {
                outputDir.convention = 'dist'
                globals.convention = [:]
                objectFactory.convention = DefaultRegistryObjectFactory.Builder.withDefaults()
            }
        }
    }

    final Property<String> siteName = Property.empty()
    final Property<String> baseUrl = Property.empty()
    final Property<File> outputDir = Property.empty()
    final Property<Map<String, Object>> globals = Property.empty()
    final Property<RegistryObjectFactory> objectFactory = Property.empty()

    private final Set<Provider<File>> textsDirs = []

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
        this.globals.set {
            this.globals.get() + globalsDelegate
        }
    }

    void textsDir(File textsDir) {
        this.textsDirs.add { textsDir }
    }

    void textsDir(Provider<File> textsDirProvider) {
        this.textsDirs << textsDirProvider
    }

    void textsDirs(Set<Provider<File>> textsDirProviders) {
        textsDirProviders.each { this.textsDir(it) }
    }

    SetProvider<File> getTextsDirs() {
        new DefaultSetProvider(this.textsDirs)
    }

}
