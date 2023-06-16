package com.jessebrault.ssg.buildscript.delegates

import com.jessebrault.ssg.SiteSpec
import com.jessebrault.ssg.buildscript.Build
import com.jessebrault.ssg.buildscript.BuildIntermediate
import com.jessebrault.ssg.buildscript.BuildMonoids
import com.jessebrault.ssg.buildscript.BuildSpec
import com.jessebrault.ssg.buildscript.OutputDir
import com.jessebrault.ssg.buildscript.SourceProviders
import com.jessebrault.ssg.buildscript.SyntheticBuildIntermediate
import com.jessebrault.ssg.buildscript.TypesContainer
import com.jessebrault.ssg.provider.CollectionProvider
import com.jessebrault.ssg.provider.CollectionProviders
import com.jessebrault.ssg.task.TaskFactory
import com.jessebrault.ssg.task.TaskFactorySpec
import com.jessebrault.ssg.text.Text
import com.jessebrault.ssg.text.TextTypes
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

import java.util.function.Function
import java.util.function.Supplier

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

@ExtendWith(MockitoExtension)
final class BuildDelegateTests {

    private final BuildDelegate d = new BuildDelegate()

    private BuildIntermediate getResults(BuildIntermediate parent = SyntheticBuildIntermediate.getEmpty()) {
        new BuildDelegate.BuildDelegateBuildIntermediate(
                BuildSpec.getEmpty(),
                this.d,
                parent,
                BuildMonoids.siteSpecMonoid,
                BuildMonoids.globalsMonoid,
                BuildMonoids.typesMonoid,
                BuildMonoids.sourcesMonoid,
                BuildMonoids.taskFactoriesMonoid,
                BuildMonoids.includedBuildsMonoid
        )
    }

    @Test
    void simpleOutputDirFunction(@Mock Function<Build, OutputDir> expected) {
        this.d.outputDirFunction = expected
        def r = this.getResults().outputDirFunction
        assertEquals(expected, r)
    }

    @Test
    void mappedOutputDirFunction() {
        final Function<Build, OutputDir> base = { Build b ->
            new OutputDir('test')
        }
        this.d.outputDirFunction {
            it.andThen { new OutputDir(it.asString() + '/nested') }
        }
        def r = this.getResults(SyntheticBuildIntermediate.get(outputDirFunction: base))
                .outputDirFunction
        def outputDir = r.apply(Build.getEmpty())
        assertEquals('test/nested', outputDir.asString())
    }

    @Test
    void siteSpecNoBase() {
        this.d.siteSpec {
            name = 'test'
            baseUrl = 'testUrl'
        }
        def r = this.getResults().siteSpec
        assertEquals('test', r.name)
        assertEquals('testUrl', r.baseUrl)
    }

    @Test
    void mappedSiteSpec() {
        this.d.siteSpec { base ->
            name = base.name + 'Preview'
            baseUrl = base.baseUrl + '/preview'
        }
        def r = this.getResults(
                SyntheticBuildIntermediate.get(siteSpec: new SiteSpec('mySite', 'https://mysite.com'))
        ).siteSpec
        assertEquals('mySitePreview', r.name)
        assertEquals('https://mysite.com/preview', r.baseUrl)
    }

    @Test
    void siteSpecBaseConcat() {
        this.d.siteSpec {
            name = '123'
        }
        def r = this.getResults(
                SyntheticBuildIntermediate.get(siteSpec: new SiteSpec('', '456'))
        ).siteSpec
        assertEquals('123', r.name)
        assertEquals('456', r.baseUrl)
    }

    @Test
    void siteSpecBaseNoConcat() {
        this.d.siteSpec(false) {
            name = '123'
        }
        def r = this.getResults(
                SyntheticBuildIntermediate.get(siteSpec: new SiteSpec('', '456'))
        ).siteSpec
        assertEquals('123', r.name)
        assertEquals(SiteSpec.DEFAULT_MONOID.zero.baseUrl, r.baseUrl)
    }

    @Test
    void emptySiteSpec() {
        assertEquals(SiteSpec.getBlank(), this.getResults().siteSpec)
    }

    @Test
    void globalsNoBase() {
        this.d.globals {
            test = 'abc'
        }
        def r = this.getResults().globals
        assertEquals([test: 'abc'], r)
    }

    @Test
    void globalsWithBase() {
        this.d.globals { base ->
            test = base.test
        }
        def r = this.getResults(SyntheticBuildIntermediate.get(globals: [test: 'abc'])).globals
        assertEquals([test: 'abc'], r)
    }

    @Test
    void globalsEmpty() {
        assertEquals([:], this.getResults().globals)
    }

    @Test
    void typesNoBase() {
        this.d.types {
            textTypes << TextTypes.MARKDOWN
        }
        def r = this.getResults().types
        assertEquals(TypesContainer.get(textTypes: [TextTypes.MARKDOWN]), r)
    }

    @Test
    void typesWithBaseNoConcat() {
        this.d.types(false) { base ->
            textTypes.addAll(base.textTypes)
        }
        def r = this.getResults(
                SyntheticBuildIntermediate.get(types: TypesContainer.get(textTypes: [TextTypes.MARKDOWN]))
        ).types
        assertEquals(TypesContainer.get(textTypes: [TextTypes.MARKDOWN]), r)
    }

    @Test
    void sourceProvidersNoBase(@Mock CollectionProvider<Text> textsProvider) {
        this.d.sources { base, types ->
            texts textsProvider
        }
        def r = this.getResults().sources
        assertTrue(textsProvider in r.textsProvider)
    }

    // Cannot use Mockito for not yet understood reasons;
    // it seems that the mock is a part of an addition somewhere on the left side,
    // and because it doesn't have the method stubbed/mocked it
    // returns null. Why this confuses Groovy much later with
    // SourceProviders is not clear; perhaps because the mock
    // returns null, it throws an exception somewhere that is
    // swallowed?
    @Test
    void sourceProvidersWithBase(/* @Mock CollectionProvider<Text> textsProvider */) {
        def textsProvider = CollectionProviders.fromCollection([] as Collection<Text>)
        this.d.sources { base, types ->
            texts base.textsProvider
        }
        def r = this.getResults(
                SyntheticBuildIntermediate.get(sources: SourceProviders.get(textsProvider: textsProvider))
        ).sources
        assertTrue(textsProvider in r.textsProvider)
    }

    @Test
    void sourceProvidersMergeBase() {
        def textsProvider = CollectionProviders.fromCollection([] as Collection<Text>)
        def r = this.getResults(
                SyntheticBuildIntermediate.get(sources: SourceProviders.get(textsProvider: textsProvider))
        ).sources
        assertTrue(textsProvider in r.textsProvider)
    }

    @Test
    void taskFactoriesNoBase(@Mock Supplier<TaskFactory> taskFactorySupplier) {
        this.d.taskFactories { base, sources ->
            register('f0', taskFactorySupplier)
        }
        def r = this.getResults().taskFactorySpecs
        assertEquals(1, r.size())
        def spec0 = r[0]
        assertEquals('f0', spec0.name)
        assertEquals(taskFactorySupplier, spec0.supplier)
    }

    @Test
    void taskFactoriesWithBase(@Mock Supplier<TaskFactory> taskFactorySupplier) {
        this.d.taskFactories(false) { base, sources ->
            registerAll(base)
        }
        def r = this.getResults(
                SyntheticBuildIntermediate.get(
                        taskFactorySpecs: [new TaskFactorySpec<TaskFactory>('spec0', taskFactorySupplier, [])]
                )
        ).taskFactorySpecs
        assertEquals(1, r.size())
        def spec0 = r[0]
        assertEquals('spec0', spec0.name)
        assertEquals(taskFactorySupplier, spec0.supplier)
        assertEquals([], spec0.configurators)
    }

    @Test
    void taskFactoriesMergeBase(@Mock Supplier<TaskFactory> taskFactorySupplier) {
        def r = this.getResults(
                SyntheticBuildIntermediate.get(
                        taskFactorySpecs: [new TaskFactorySpec<TaskFactory>('spec0', taskFactorySupplier, [])]
                )
        ).taskFactorySpecs
        assertEquals(1, r.size())
        def spec0 = r[0]
        assertEquals('spec0', spec0.name)
        assertEquals(taskFactorySupplier, spec0.supplier)
        assertEquals([], spec0.configurators)
    }

    @Test
    void includedBuildsNoBase() {
        this.d.concatIncludedBuildsWithBase = false
        this.d.includeBuild('included')
        def r = this.getResults(
                SyntheticBuildIntermediate.get(includedBuilds: ['notIncluded'])
        ).includedBuilds
        assertEquals(1, r.size())
        def includedBuild0 = r[0]
        assertEquals('included', includedBuild0)
    }

    @Test
    void includedBuildsWithBase() {
        this.d.concatIncludedBuildsWithBase = true
        def r = this.getResults(
                SyntheticBuildIntermediate.get(includedBuilds: ['baseIncluded'])
        ).includedBuilds
        assertEquals(1, r.size())
        def includedBuild0 = r[0]
        assertEquals('baseIncluded', includedBuild0)
    }

}
