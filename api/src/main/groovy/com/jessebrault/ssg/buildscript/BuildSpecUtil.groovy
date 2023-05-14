package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.SiteSpec
import com.jessebrault.ssg.buildscript.delegates.BuildDelegate
import com.jessebrault.ssg.task.TaskFactory
import com.jessebrault.ssg.task.TaskFactorySpec
import com.jessebrault.ssg.util.Monoid
import com.jessebrault.ssg.util.Monoids
import com.jessebrault.ssg.util.Zero
import org.jgrapht.traverse.DepthFirstIterator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.MarkerFactory

import java.util.function.BiFunction

final class BuildSpecUtil {

    private static final Logger logger = LoggerFactory.getLogger(BuildSpecUtil)
    private static final Marker enter = MarkerFactory.getMarker('ENTER')
    private static final Marker exit = MarkerFactory.getMarker('EXIT')

    private static final Monoid<Map<String, Object>> globalsMonoid = Monoids.of([:]) { m0, m1 ->
        m0 + m1
    }

    private static final Monoid<Collection<TaskFactorySpec<TaskFactory>>> taskFactoriesMonoid =
            Monoids.getMergeCollectionMonoid(TaskFactorySpec.SAME_NAME_AND_SUPPLIER_EQ, TaskFactorySpec.DEFAULT_SEMIGROUP)

    private static <T> T reduceResults(
            Collection<BuildDelegate.Results> resultsCollection,
            Zero<T> tZero,
            BiFunction<T, BuildDelegate.Results, T> resultsToT
    ) {
        resultsCollection.inject(tZero.zero) { acc, r ->
            resultsToT.apply(acc, r)
        }
    }

    private static Collection<BuildDelegate.Results> mapBuildSpecsToResults(Collection<BuildSpec> buildSpecs) {
        buildSpecs.collect {
            def delegate = new BuildDelegate()
            it.buildClosure.delegate = delegate
            //noinspection UnnecessaryQualifiedReference
            it.buildClosure.resolveStrategy = Closure.DELEGATE_FIRST
            it.buildClosure()
            new BuildDelegate.Results(delegate)
        }
    }

    private static Build toBuild(
            Collection<BuildSpec> specs
    ) {
        if (specs.empty) {
            throw new IllegalArgumentException('specs must contain at least one BuildSpec')
        }
        def allResults = mapBuildSpecsToResults(specs)
        def outputDirFunctionResult = reduceResults(allResults, OutputDirFunctions.DEFAULT_MONOID) { acc, r ->
            r.getOutputDirFunctionResult(acc, { OutputDirFunctions.DEFAULT_MONOID.zero })
        }
        def siteSpecResult = reduceResults(allResults, SiteSpec.DEFAULT_MONOID) { acc, r ->
            r.getSiteSpecResult(acc, true, SiteSpec.DEFAULT_MONOID)
        }
        def globalsResult = reduceResults(allResults, globalsMonoid) { acc, r ->
            r.getGlobalsResult(acc, true, globalsMonoid)
        }

        def typesResult = reduceResults(allResults, TypesContainer.DEFAULT_MONOID) { acc, r ->
            r.getTypesResult(acc, true, TypesContainer.DEFAULT_MONOID)
        }
        def sourcesResult = reduceResults(allResults, SourceProviders.DEFAULT_MONOID) { acc, r ->
            r.getSourcesResult(acc, true, SourceProviders.DEFAULT_MONOID, typesResult)
        }
        def taskFactoriesResult = reduceResults(allResults, taskFactoriesMonoid) { acc, r ->
            r.getTaskFactoriesResult(acc, true, taskFactoriesMonoid, sourcesResult)
        }

        new Build(
                specs[0].name,
                outputDirFunctionResult,
                siteSpecResult,
                globalsResult,
                taskFactoriesResult
        )
    }

    static Collection<Build> getBuilds(Collection<BuildSpec> buildSpecs) {
        logger.trace(enter, '')
        def graph = BuildGraphUtil.getDependencyGraph(buildSpecs)
        def r = new DepthFirstIterator<>(graph).findResults {
            if (it.isAbstract) {
                return null
            }
            def ancestors = BuildGraphUtil.getAncestors(it, graph)
            logger.debug('ancestors of {}: {}', it, ancestors)
            toBuild([*ancestors, it])
        }
        logger.trace(exit, 'r: {}', r)
        r
    }

    private BuildSpecUtil() {}

}
