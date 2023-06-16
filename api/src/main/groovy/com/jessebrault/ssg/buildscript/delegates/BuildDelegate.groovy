package com.jessebrault.ssg.buildscript.delegates

import com.jessebrault.ssg.SiteSpec
import com.jessebrault.ssg.buildscript.*
import com.jessebrault.ssg.mutable.Mutable
import com.jessebrault.ssg.mutable.Mutables
import com.jessebrault.ssg.task.TaskFactory
import com.jessebrault.ssg.task.TaskFactorySpec
import com.jessebrault.ssg.util.Monoid
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString
import groovy.transform.stc.SimpleType

import java.util.function.Function
import java.util.function.UnaryOperator

@NullCheck(includeGenerated = true)
@EqualsAndHashCode(includeFields = true)
final class BuildDelegate {

    @TupleConstructor(includeFields = true, defaults = false)
    @NullCheck(includeGenerated = true)
    @EqualsAndHashCode(includeFields = true)
    static final class BuildDelegateBuildIntermediate implements BuildIntermediate {

        private final BuildSpec buildSpec
        private final BuildDelegate delegate
        private final BuildIntermediate parent

        private final Monoid<SiteSpec> siteSpecMonoid
        private final Monoid<Map<String, Object>> globalsMonoid
        private final Monoid<TypesContainer> typesMonoid
        private final Monoid<SourceProviders> sourcesMonoid
        private final Monoid<Collection<TaskFactorySpec<TaskFactory>>> taskFactoriesMonoid
        private final Monoid<Collection<String>> includedBuildsMonoid

        @Override
        BuildSpec getBuildSpec() {
            this.buildSpec
        }

        @Override
        Function<Build, OutputDir> getOutputDirFunction() {
            this.delegate.outputDirFunction.getOrElse {
                this.delegate.outputDirFunctionMapper.match({ parent.outputDirFunction }) {
                    it.apply(this.parent.outputDirFunction)
                }
            }
        }

        @Override
        SiteSpec getSiteSpec() {
            def siteSpecClosure = this.delegate.siteSpecClosure.getOrElse { return { } }
            def d = new SiteSpecDelegate(this.siteSpecMonoid)
            siteSpecClosure.delegate = d
            siteSpecClosure.resolveStrategy = Closure.DELEGATE_FIRST
            siteSpecClosure(this.parent.siteSpec)
            if (this.delegate.siteSpecConcatBase.getOrElse { true }) {
                this.siteSpecMonoid.concat.apply(this.parent.siteSpec, d.result)
            } else {
                d.result
            }
        }

        @Override
        Map<String, Object> getGlobals() {
            def globalsClosure = this.delegate.globalsClosure.getOrElse { return { } }
            def d = new GlobalsDelegate()
            globalsClosure.delegate = d
            globalsClosure.resolveStrategy = Closure.DELEGATE_FIRST
            globalsClosure(this.parent.globals)
            if (this.delegate.globalsConcatBase.getOrElse { true }) {
                this.globalsMonoid.concat.apply(this.parent.globals, d.getResult())
            } else {
                d.getResult()
            }
        }

        @Override
        TypesContainer getTypes() {
            def typesClosure = this.delegate.typesClosure.getOrElse { return { } }
            def d = new TypesDelegate()
            typesClosure.delegate = d
            typesClosure.resolveStrategy = Closure.DELEGATE_FIRST
            typesClosure(this.parent.types)
            if (this.delegate.typesConcatBase.getOrElse { true }) {
                this.typesMonoid.concat.apply(this.parent.types, d.result)
            } else {
                d.result
            }
        }

        @Override
        SourceProviders getSources() {
            def sourcesClosure = this.delegate.sourcesClosure.getOrElse { return { base, types -> } }
            def d = new SourceProvidersDelegate()
            sourcesClosure.delegate = d
            sourcesClosure.resolveStrategy = Closure.DELEGATE_FIRST
            sourcesClosure(this.parent.sources, this.types)
            if (this.delegate.sourcesConcatBase.getOrElse { true }) {
                this.sourcesMonoid.concat.apply(this.parent.sources, d.result)
            } else {
                d.result
            }
        }

        @Override
        Collection<TaskFactorySpec<TaskFactory>> getTaskFactorySpecs() {
            def taskFactoriesClosure = this.delegate.taskFactoriesClosure.getOrElse { return { base, sources -> } }
            def d = new TaskFactoriesDelegate()
            taskFactoriesClosure.delegate = d
            taskFactoriesClosure.resolveStrategy = Closure.DELEGATE_FIRST
            taskFactoriesClosure(this.parent.taskFactorySpecs, this.sources)
            if (this.delegate.taskFactoriesConcatBase.getOrElse { true }) {
                this.taskFactoriesMonoid.concat.apply(this.parent.taskFactorySpecs, d.result)
            } else {
                d.result
            }
        }

        @Override
        Collection<String> getIncludedBuilds() {
            if (this.delegate.includedBuildsConcatBase.getOrElse { true }) {
                this.includedBuildsMonoid.concat.apply(this.parent.includedBuilds, this.delegate.includedBuilds)
            } else {
                this.delegate.includedBuilds
            }
        }

    }

    private final Mutable<Function<Build, OutputDir>> outputDirFunction = Mutables.getEmpty()
    private final Mutable<UnaryOperator<Function<Build, OutputDir>>> outputDirFunctionMapper = Mutables.getEmpty()

    private final Mutable<Boolean> siteSpecConcatBase = Mutables.getEmpty()
    private final Mutable<Closure<?>> siteSpecClosure = Mutables.getEmpty()

    private final Mutable<Boolean> globalsConcatBase = Mutables.getEmpty()
    private final Mutable<Closure<?>> globalsClosure = Mutables.getEmpty()

    private final Mutable<Boolean> typesConcatBase = Mutables.getEmpty()
    private final Mutable<Closure<?>> typesClosure = Mutables.getEmpty()

    private final Mutable<Boolean> sourcesConcatBase = Mutables.getEmpty()
    private final Mutable<Closure<?>> sourcesClosure = Mutables.getEmpty()

    private final Mutable<Boolean> taskFactoriesConcatBase = Mutables.getEmpty()
    private final Mutable<Closure<?>> taskFactoriesClosure = Mutables.getEmpty()

    private final Mutable<Boolean> includedBuildsConcatBase = Mutables.getEmpty()
    private final Collection<String> includedBuilds = []

    void setOutputDirFunction(Function<Build, OutputDir> outputDirFunction) {
        this.outputDirFunction.set(outputDirFunction)
    }

    void setOutputDir(File file) {
        this.outputDirFunction.set { new OutputDir(file) }
    }

    void setOutputDir(String path) {
        this.outputDirFunction.set { new OutputDir(path) }
    }

    // Maps the *base*
    void outputDirFunction(UnaryOperator<Function<Build, OutputDir>> mapper) {
        this.outputDirFunctionMapper.set(mapper)
    }

    void siteSpec(
            @DelegatesTo(value = SiteSpecDelegate, strategy = Closure.DELEGATE_FIRST)
            @ClosureParams(value = SimpleType, options = 'com.jessebrault.ssg.SiteSpec')
            Closure<?> siteSpecClosure
    ) {
        this.siteSpec(true, siteSpecClosure)
    }

    void siteSpec(
            boolean concatWithBase,
            @DelegatesTo(value = SiteSpecDelegate, strategy = Closure.DELEGATE_FIRST)
            @ClosureParams(value = SimpleType, options = 'com.jessebrault.ssg.SiteSpec')
            Closure<?> siteSpecClosure
    ) {
        this.siteSpecConcatBase.set(concatWithBase)
        this.siteSpecClosure.set(siteSpecClosure)
    }

    void globals(
            @DelegatesTo(value = GlobalsDelegate, strategy = Closure.DELEGATE_FIRST)
            @ClosureParams(value = FromString, options = 'Map<String, Object>')
            Closure<?> globalsClosure
    ) {
        this.globals(true, globalsClosure)
    }

    void globals(
            boolean concatWithBase,
            @DelegatesTo(value = GlobalsDelegate, strategy = Closure.DELEGATE_FIRST)
            @ClosureParams(value = FromString, options = 'Map<String, Object>')
            Closure<?> globalsClosure
    ) {
        this.globalsConcatBase.set(concatWithBase)
        this.globalsClosure.set(globalsClosure)
    }

    void types(
            @DelegatesTo(value = TypesDelegate, strategy = Closure.DELEGATE_FIRST)
            @ClosureParams(value = SimpleType, options = 'com.jessebrault.ssg.buildscript.TypesContainer')
            Closure<?> typesClosure
    ) {
        this.types(true, typesClosure)
    }

    void types(
            boolean concatWithBase,
            @DelegatesTo(value = TypesDelegate, strategy = Closure.DELEGATE_FIRST)
            @ClosureParams(value = SimpleType, options = 'com.jessebrault.ssg.buildscript.TypesContainer')
            Closure<?> typesClosure
    ) {
        this.typesConcatBase.set(concatWithBase)
        this.typesClosure.set(typesClosure)
    }

    void sources(
            @DelegatesTo(value = SourceProvidersDelegate, strategy = Closure.DELEGATE_FIRST)
            @ClosureParams(
                    value = FromString,
                    options = 'com.jessebrault.ssg.buildscript.SourceProviders, com.jessebrault.ssg.buildscript.TypesContainer'
            )
            Closure<?> sourcesClosure
    ) {
        this.sources(true, sourcesClosure)
    }

    void sources(
            boolean concatWithBase,
            @DelegatesTo(value = SourceProvidersDelegate, strategy = Closure.DELEGATE_FIRST)
            @ClosureParams(
                    value = FromString,
                    options = 'com.jessebrault.ssg.buildscript.SourceProviders, com.jessebrault.ssg.buildscript.TypesContainer'
            )
            Closure<?> sourcesClosure
    ) {
        this.sourcesConcatBase.set(concatWithBase)
        this.sourcesClosure.set(sourcesClosure)
    }

    void taskFactories(
            @DelegatesTo(value = TaskFactoriesDelegate, strategy = Closure.DELEGATE_FIRST)
            @ClosureParams(
                    value = FromString,
                    options = 'java.util.Collection<com.jessebrault.ssg.task.TaskFactorySpec<com.jessebrault.ssg.task.TaskFactory>>, com.jessebrault.ssg.buildscript.SourceProviders'
            )
            Closure<?> taskFactoriesClosure
    ) {
        this.taskFactories(true, taskFactoriesClosure)
    }

    void taskFactories(
            boolean concatWithBase,
            @DelegatesTo(value = TaskFactoriesDelegate, strategy = Closure.DELEGATE_FIRST)
            @ClosureParams(
                    value = FromString,
                    options = 'java.util.Collection<com.jessebrault.ssg.task.TaskFactorySpec<com.jessebrault.ssg.task.TaskFactory>>, com.jessebrault.ssg.buildscript.SourceProviders'
            )
            Closure<?> taskFactoriesClosure
    ) {
        this.taskFactoriesConcatBase.set(concatWithBase)
        this.taskFactoriesClosure.set(taskFactoriesClosure)
    }

    void setConcatIncludedBuildsWithBase(boolean value) {
        this.includedBuildsConcatBase.set(value)
    }

    void includeBuild(String buildName) {
        this.includedBuilds << buildName
    }

}
