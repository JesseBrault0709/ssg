package com.jessebrault.ssg.buildscript.delegates

import com.jessebrault.ssg.SiteSpec
import com.jessebrault.ssg.buildscript.Build
import com.jessebrault.ssg.buildscript.OutputDir
import com.jessebrault.ssg.buildscript.SourceProviders
import com.jessebrault.ssg.buildscript.TypesContainer
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
import java.util.function.Supplier
import java.util.function.UnaryOperator

@NullCheck(includeGenerated = true)
@EqualsAndHashCode(includeFields = true)
final class BuildDelegate {

    @TupleConstructor(includeFields = true, defaults = false)
    @NullCheck(includeGenerated = true)
    @EqualsAndHashCode(includeFields = true)
    static final class Results {

        private final BuildDelegate delegate

        Function<Build, OutputDir> getOutputDirFunctionResult(
                Function<Build, OutputDir> base,
                Supplier<Function<Build, OutputDir>> onEmpty
        ) {
            this.delegate.outputDirFunction.getOrElse {
                this.delegate.outputDirFunctionMapper.match(onEmpty) {
                    it.apply(base)
                }
            }
        }

        SiteSpec getSiteSpecResult(
                SiteSpec base,
                boolean onConcatWithBaseEmpty,
                Monoid<SiteSpec> siteSpecMonoid
        ) {
            def concatWithBase = this.delegate.siteSpecConcatBase.isPresent()
                    ? this.delegate.siteSpecConcatBase.get()
                    : onConcatWithBaseEmpty
            def onEmpty = { concatWithBase ? base : siteSpecMonoid.zero }
            this.delegate.siteSpecClosure.match(onEmpty) {
                def d = new SiteSpecDelegate(siteSpecMonoid)
                it.delegate = d
                //noinspection UnnecessaryQualifiedReference
                it.resolveStrategy = Closure.DELEGATE_FIRST
                it(base)
                def r = d.getResult()
                concatWithBase ? siteSpecMonoid.concat.apply(base, r) : r
            }
        }

        Map<String, Object> getGlobalsResult(
                Map<String, Object> base,
                boolean onConcatWithBaseEmpty,
                Monoid<Map<String, Object>> globalsMonoid
        ) {
            def concatWithBase = this.delegate.globalsConcatBase.isPresent()
                    ? this.delegate.globalsConcatBase.get()
                    : onConcatWithBaseEmpty
            def onEmpty = { concatWithBase ? base : globalsMonoid.zero }
            this.delegate.globalsClosure.match(onEmpty) {
                def d = new GlobalsDelegate()
                it.delegate = d
                //noinspection UnnecessaryQualifiedReference
                it.resolveStrategy = Closure.DELEGATE_FIRST
                it(base)
                def r = d.getResult()
                concatWithBase ? globalsMonoid.concat.apply(base, r) : r
            }
        }

        TypesContainer getTypesResult(
                TypesContainer base,
                boolean onConcatWithBaseEmpty,
                Monoid<TypesContainer> typesContainerMonoid
        ) {
            def concatWithBase = this.delegate.typesConcatBase.isPresent()
                    ? this.delegate.typesConcatBase.get()
                    : onConcatWithBaseEmpty
            def onEmpty = { concatWithBase ? base : typesContainerMonoid.zero }
            this.delegate.typesClosure.match(onEmpty) {
                def d = new TypesDelegate()
                it.delegate = d
                //noinspection UnnecessaryQualifiedReference
                it.resolveStrategy = Closure.DELEGATE_FIRST
                it(base)
                def r = d.getResult()
                concatWithBase ? typesContainerMonoid.concat.apply(base, r) : r
            }
        }

        SourceProviders getSourcesResult(
                SourceProviders base,
                boolean onConcatWithBaseEmpty,
                Monoid<SourceProviders> sourceProvidersMonoid,
                TypesContainer types
        ) {
            def concatWithBase = this.delegate.sourcesConcatBase.isPresent()
                    ? this.delegate.sourcesConcatBase.get()
                    : onConcatWithBaseEmpty
            def onEmpty = { concatWithBase ? base : sourceProvidersMonoid.zero }
            this.delegate.sourcesClosure.match(onEmpty) {
                def d = new SourceProvidersDelegate()
                it.delegate = d
                //noinspection UnnecessaryQualifiedReference
                it.resolveStrategy = Closure.DELEGATE_FIRST
                it(base, types)
                def r = d.getResult()
                concatWithBase ? sourceProvidersMonoid.concat.apply(base, r) : r
            }
        }

        Collection<TaskFactorySpec<TaskFactory>> getTaskFactoriesResult(
                Collection<TaskFactorySpec<TaskFactory>> base,
                boolean onConcatWithBaseEmpty,
                Monoid<Collection<TaskFactorySpec<TaskFactory>>> taskFactorySpecsMonoid,
                SourceProviders sources
        ) {
            def concatWithBase = this.delegate.taskFactoriesConcatBase.isPresent()
                    ? this.delegate.taskFactoriesConcatBase.get()
                    : onConcatWithBaseEmpty
            def onEmpty = { concatWithBase ? base : taskFactorySpecsMonoid.zero }
            this.delegate.taskFactoriesClosure.match(onEmpty) {
                def d = new TaskFactoriesDelegate()
                it.delegate = d
                //noinspection UnnecessaryQualifiedReference
                it.resolveStrategy = Closure.DELEGATE_FIRST
                it(base, sources)
                def r = d.getResult()
                concatWithBase ? taskFactorySpecsMonoid.concat.apply(base, r) : r
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

}
