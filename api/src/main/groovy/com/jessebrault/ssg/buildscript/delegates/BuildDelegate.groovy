package com.jessebrault.ssg.buildscript.delegates

import com.jessebrault.ssg.model.Model
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groowt.util.fp.property.Property
import groowt.util.fp.provider.DefaultNamedProvider
import groowt.util.fp.provider.DefaultNamedSetProvider
import groowt.util.fp.provider.DefaultSetProvider
import groowt.util.fp.provider.NamedProvider
import groowt.util.fp.provider.NamedSetProvider
import groowt.util.fp.provider.Provider
import groowt.util.fp.provider.SetProvider

@NullCheck(includeGenerated = true)
@EqualsAndHashCode(includeFields = true)
final class BuildDelegate {

    final Property<String> siteName = Property.empty().tap {
        convention = 'An Ssg Site'
    }

    final Property<String> baseUrl = Property.empty().tap {
        convention = ''
    }

    final Property<File> outputDir = Property.empty().tap {
        convention = siteName.map { it.replace(' ', '-').toLowerCase() + '-build' }
    }

    final Property<Map<String, Object>> globals = Property.empty().tap {
        convention = [:]
    }

    private final Set<Provider<File>> textsDirs = []
    private final Set<NamedProvider<Model>> models = []

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

    void globals(@DelegatesTo(value = GlobalsDelegate, strategy = Closure.DELEGATE_FIRST) Closure<?> globalsClosure) {
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

    void models(@DelegatesTo(ModelsDelegate) Closure modelsClosure) {
        def modelsDelegate = new ModelsDelegate()
        modelsClosure.delegate = modelsDelegate
        modelsClosure()
        models.addAll(modelsDelegate.result)
    }

    void models(Set<NamedProvider<Model>> models) {
        this.models.addAll(models)
    }

    NamedSetProvider<Model> getModels() {
        new DefaultNamedSetProvider(this.models)
    }

}
