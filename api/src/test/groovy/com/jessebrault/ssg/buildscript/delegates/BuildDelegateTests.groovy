package com.jessebrault.ssg.buildscript.delegates

import com.jessebrault.ssg.SiteSpec
import com.jessebrault.ssg.buildscript.Build
import com.jessebrault.ssg.buildscript.OutputDir
import com.jessebrault.ssg.buildscript.SourceProviders
import com.jessebrault.ssg.buildscript.TypesContainer
import com.jessebrault.ssg.provider.CollectionProvider
import com.jessebrault.ssg.provider.CollectionProviders
import com.jessebrault.ssg.task.TaskFactory
import com.jessebrault.ssg.task.TaskFactorySpec
import com.jessebrault.ssg.text.Text
import com.jessebrault.ssg.text.TextTypes
import com.jessebrault.ssg.util.Monoid
import com.jessebrault.ssg.util.Monoids
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

    private BuildDelegate.Results getResults() {
        new BuildDelegate.Results(this.d)
    }

    @Test
    void simpleOutputDirFunction(
            @Mock Function<Build, OutputDir> expected,
            @Mock Function<Build, OutputDir> base,
            @Mock Supplier<Function<Build, OutputDir>> onEmpty
    ) {
        this.d.outputDirFunction = expected
        def r = this.results.getOutputDirFunctionResult(base, onEmpty)
        assertEquals(expected, r)
    }

    @Test
    void mappedOutputDirFunction(
            @Mock Supplier<Function<Build, OutputDir>> onEmpty
    ) {
        final Function<Build, OutputDir> base = { Build b ->
            new OutputDir('test')
        }
        this.d.outputDirFunction {
            it.andThen { new OutputDir(it.asString() + '/nested') }
        }
        def r = this.results.getOutputDirFunctionResult(base, onEmpty)
        def outputDir = r.apply(Build.getEmpty())
        assertEquals('test/nested', outputDir.asString())
    }

    @Test
    void emptyOutputDirFunction(
            @Mock Function<Build, OutputDir> base,
            @Mock Function<Build, OutputDir> empty
    ) {
        assertEquals(empty, this.results.getOutputDirFunctionResult(base, { empty }))
    }

    @Test
    void siteSpecNoBase() {
        this.d.siteSpec {
            name = 'test'
            baseUrl = 'testUrl'
        }
        def r = this.results.getSiteSpecResult(
                SiteSpec.getBlank(),
                true,
                SiteSpec.DEFAULT_MONOID
        )
        assertEquals('test', r.name)
        assertEquals('testUrl', r.baseUrl)
    }

    @Test
    void mappedSiteSpec() {
        this.d.siteSpec { base ->
            name = base.name + 'Preview'
            baseUrl = base.baseUrl + '/preview'
        }
        def r = this.results.getSiteSpecResult(
                new SiteSpec('mySite', 'https://mysite.com'),
                true,
                SiteSpec.DEFAULT_MONOID
        )
        assertEquals('mySitePreview', r.name)
        assertEquals('https://mysite.com/preview', r.baseUrl)
    }

    @Test
    void siteSpecBaseConcat() {
        this.d.siteSpec {
            name = '123'
        }
        def r = this.results.getSiteSpecResult(
                new SiteSpec('', '456'),
                true,
                SiteSpec.DEFAULT_MONOID
        )
        assertEquals('123', r.name)
        assertEquals('456', r.baseUrl)
    }

    @Test
    void siteSpecBaseNoConcat() {
        this.d.siteSpec(false) {
            name = '123'
        }
        def r = this.results.getSiteSpecResult(
                new SiteSpec('', '456'),
                true,
                SiteSpec.DEFAULT_MONOID
        )
        assertEquals('123', r.name)
        assertEquals(SiteSpec.DEFAULT_MONOID.zero.baseUrl, r.baseUrl)
    }

    @Test
    void emptySiteSpec() {
        assertEquals(
                SiteSpec.getBlank(),
                this.results.getSiteSpecResult(SiteSpec.getBlank(), true, SiteSpec.DEFAULT_MONOID)
        )
    }

    private static Monoid<Map<String, Object>> getGlobalsMonoid() {
        Monoids.of([:]) { m0, m1 -> m0 + m1 }
    }

    @Test
    void globalsNoBase() {
        this.d.globals {
            test = 'abc'
        }
        def r = this.results.getGlobalsResult([:], true, getGlobalsMonoid())
        assertEquals([test: 'abc'], r)
    }

    @Test
    void globalsWithBase() {
        this.d.globals { base ->
            test = base.test
        }
        def r = this.results.getGlobalsResult(
                [test: 'abc'],
                true,
                getGlobalsMonoid()
        )
        assertEquals([test: 'abc'], r)
    }

    @Test
    void globalsEmpty() {
        assertEquals([:], this.results.getGlobalsResult([:], true, getGlobalsMonoid()))
    }

    @Test
    void typesNoBase() {
        this.d.types {
            textTypes << TextTypes.MARKDOWN
        }
        def r = this.results.getTypesResult(
                TypesContainer.getEmpty(),
                true,
                TypesContainer.DEFAULT_MONOID
        )
        assertEquals(TypesContainer.get(textTypes: [TextTypes.MARKDOWN]), r)
    }

    @Test
    void typesWithBaseNoConcat() {
        this.d.types(false) { base ->
            textTypes.addAll(base.textTypes)
        }
        def r = this.results.getTypesResult(
                TypesContainer.get(textTypes: [TextTypes.MARKDOWN]),
                true,
                TypesContainer.DEFAULT_MONOID
        )
        assertEquals(TypesContainer.get(textTypes: [TextTypes.MARKDOWN]), r)
    }

    @Test
    void sourceProvidersNoBase(@Mock CollectionProvider<Text> textsProvider) {
        this.d.sources { base, types ->
            texts textsProvider
        }
        def r = this.results.getSourcesResult(
                SourceProviders.getEmpty(),
                true,
                SourceProviders.DEFAULT_MONOID,
                TypesContainer.getEmpty()
        )
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
        def r = this.results.getSourcesResult(
                SourceProviders.get(textsProvider: textsProvider),
                true,
                SourceProviders.DEFAULT_MONOID,
                TypesContainer.getEmpty()
        )
        assertTrue(textsProvider in r.textsProvider)
    }

    @Test
    void sourceProvidersMergeBase() {
        def textsProvider = CollectionProviders.fromCollection([] as Collection<Text>)
        def r = this.results.getSourcesResult(
                SourceProviders.get(textsProvider: textsProvider),
                true,
                SourceProviders.DEFAULT_MONOID,
                TypesContainer.getEmpty()
        )
        assertTrue(textsProvider in r.textsProvider)
    }

    private static Monoid<Collection<TaskFactorySpec<TaskFactory>>> getTaskFactoriesMonoid() {
        Monoids.of([] as Collection<TaskFactorySpec<TaskFactory>>) { c0, c1 ->
            c0 + c1
        }
    }

    @Test
    void taskFactoriesNoBase(@Mock Supplier<TaskFactory> taskFactorySupplier) {
        this.d.taskFactories { base, sources ->
            register('f0', taskFactorySupplier)
        }
        def r = this.results.getTaskFactoriesResult(
                [],
                true,
                getTaskFactoriesMonoid(),
                SourceProviders.getEmpty()
        )
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
        def r = this.results.getTaskFactoriesResult(
                [new TaskFactorySpec<TaskFactory>('spec0', taskFactorySupplier, [])],
                true,
                getTaskFactoriesMonoid(),
                SourceProviders.getEmpty()
        )
        assertEquals(1, r.size())
        def spec0 = r[0]
        assertEquals('spec0', spec0.name)
        assertEquals(taskFactorySupplier, spec0.supplier)
        assertEquals([], spec0.configurators)
    }

    @Test
    void taskFactoriesMergeBase(@Mock Supplier<TaskFactory> taskFactorySupplier) {
        def r = this.results.getTaskFactoriesResult(
                [new TaskFactorySpec<TaskFactory>('spec0', taskFactorySupplier, [])],
                true,
                getTaskFactoriesMonoid(),
                SourceProviders.getEmpty()
        )
        assertEquals(1, r.size())
        def spec0 = r[0]
        assertEquals('spec0', spec0.name)
        assertEquals(taskFactorySupplier, spec0.supplier)
        assertEquals([], spec0.configurators)
    }

    private static Monoid<Collection<String>> getIncludedBuildsMonoid() {
        Monoids.of([]) { c0, c1 -> c0 + c1 }
    }

    @Test
    void includedBuildsNoBase() {
        this.d.concatIncludedBuildsWithBase = false
        this.d.includeBuild('included')
        def r = this.results.getIncludedBuildsResult(
                ['notIncluded'], true, getIncludedBuildsMonoid()
        )
        assertEquals(1, r.size())
        def includedBuild0 = r[0]
        assertEquals('included', includedBuild0)
    }

    @Test
    void includedBuildsWithBase() {
        this.d.concatIncludedBuildsWithBase = true
        def r = this.results.getIncludedBuildsResult(
                ['baseIncluded'], true, getIncludedBuildsMonoid()
        )
        assertEquals(1, r.size())
        def includedBuild0 = r[0]
        assertEquals('baseIncluded', includedBuild0)
    }

}
