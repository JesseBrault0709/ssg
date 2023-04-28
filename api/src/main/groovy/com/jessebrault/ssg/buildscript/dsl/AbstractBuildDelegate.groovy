package com.jessebrault.ssg.buildscript.dsl

import com.jessebrault.ssg.SiteSpec
import com.jessebrault.ssg.buildscript.SourceProviders
import com.jessebrault.ssg.buildscript.TypesContainer
import com.jessebrault.ssg.task.TaskFactory
import com.jessebrault.ssg.task.TaskFactorySpec
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType

abstract class AbstractBuildDelegate<T> {

    private final Collection<Closure<?>> siteSpecClosures = []
    private final Collection<Closure<?>> globalsClosures = []
    private final Collection<Closure<?>> typesClosures = []
    private final Collection<Closure<?>> sourcesClosures = []
    private final Collection<Closure<?>> taskFactoriesClosures = []

    abstract T getResult()

    protected final SiteSpec getSiteSpecResult() {
        this.siteSpecClosures.inject(SiteSpec.getBlank()) { acc, closure ->
            def d = new SiteSpecDelegate()
            closure.delegate = d
            //noinspection UnnecessaryQualifiedReference
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure()
            acc + d.getResult()
        }
    }

    protected final Map<String, Object> getGlobalsResult() {
        this.globalsClosures.inject([:] as Map<String, Object>) { acc, closure ->
            def d = new GlobalsDelegate()
            closure.delegate = d
            //noinspection UnnecessaryQualifiedReference
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure()
            acc + d.getResult()
        }
    }

    protected final TypesContainer getTypesResult() {
        this.typesClosures.inject(TypesContainer.getEmpty()) { acc, closure ->
            def d = new TypesDelegate()
            closure.delegate = d
            //noinspection UnnecessaryQualifiedReference
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure()
            acc + d.getResult()
        }
    }

    protected final SourceProviders getSourcesResult(TypesContainer typesContainer) {
        this.sourcesClosures.inject(SourceProviders.getEmpty()) { acc, closure ->
            def d = new SourceProvidersDelegate()
            closure.delegate = d
            //noinspection UnnecessaryQualifiedReference
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure(typesContainer)
            acc + d.getResult()
        }
    }

    protected final Collection<TaskFactorySpec<TaskFactory>> getTaskFactoriesResult(SourceProviders sourceProviders) {
       this.taskFactoriesClosures.inject([:] as Map<String, TaskFactorySpec>) { acc, closure ->
            def d = new TaskFactoriesDelegate()
            closure.delegate = d
           //noinspection UnnecessaryQualifiedReference
           closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure(sourceProviders)
            def specs = d.getResult()
            specs.forEach { name, spec ->
                acc.merge(name, spec) { spec0, spec1 -> spec0 + spec1 }
            }
            acc
        }.values()
    }

    void siteSpec(
            @DelegatesTo(value = SiteSpecDelegate, strategy = Closure.DELEGATE_FIRST)
            Closure<?> siteSpecClosure
    ) {
        this.siteSpecClosures << siteSpecClosure
    }

    void globals(
            @DelegatesTo(value = GlobalsDelegate, strategy = Closure.DELEGATE_FIRST)
            Closure<?> globalsClosure
    ) {
        this.globalsClosures << globalsClosure
    }

    void types(
            @DelegatesTo(value = TypesDelegate, strategy = Closure.DELEGATE_FIRST)
            Closure<?> typesClosure
    ) {
        this.typesClosures << typesClosure
    }

    void providers(
            @DelegatesTo(value = SourceProvidersDelegate, strategy = Closure.DELEGATE_FIRST)
            @ClosureParams(value = SimpleType, options = 'com.jessebrault.ssg.buildscript.TypesContainer')
            Closure<?> providersClosure
    ) {
        this.sourcesClosures << providersClosure
    }

    void taskFactories(
            @DelegatesTo(value = TaskFactoriesDelegate, strategy = Closure.DELEGATE_FIRST)
            @ClosureParams(value = SimpleType, options = 'com.jessebrault.ssg.buildscript.SourceProviders')
            Closure<?> taskFactoriesClosure
    ) {
        this.taskFactoriesClosures << taskFactoriesClosure
    }

}
