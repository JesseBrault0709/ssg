package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.buildscript.delegates.BuildDelegate
import com.jessebrault.ssg.util.Monoid
import groovy.transform.NullCheck
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.MarkerFactory

import java.util.function.BiFunction

@NullCheck
final class BuildSpecUtil {

    private static final Logger logger = LoggerFactory.getLogger(BuildSpecUtil)
    private static final Marker enter = MarkerFactory.getMarker('ENTER')
    private static final Marker exit = MarkerFactory.getMarker('EXIT')

    private static Build intermediateToBuild(BuildIntermediate intermediate) {
        Build.get(
                name: intermediate.buildSpec.name,
                outputDirFunction: intermediate.outputDirFunction,
                siteSpec: intermediate.siteSpec,
                globals: intermediate.globals,
                taskFactorySpecs: intermediate.taskFactorySpecs,
                includedBuilds: intermediate.includedBuilds
        )
    }

    private static BuildIntermediate specToIntermediate(BuildSpec buildSpec, BuildIntermediate parent) {
        def d = new BuildDelegate()
        buildSpec.buildClosure.delegate = d
        buildSpec.buildClosure.resolveStrategy = Closure.DELEGATE_FIRST
        buildSpec.buildClosure()
        new BuildDelegate.BuildDelegateBuildIntermediate(
                buildSpec,
                d,
                parent,
                BuildMonoids.siteSpecMonoid,
                BuildMonoids.globalsMonoid,
                BuildMonoids.typesMonoid,
                BuildMonoids.sourcesMonoid,
                BuildMonoids.taskFactoriesMonoid,
                BuildMonoids.includedBuildsMonoid
        )
    }

    private static BuildIntermediate reduceWithParents(
            BuildSpec rootSpec,
            BuildGraph buildGraph,
            Monoid<BuildIntermediate> buildIntermediateMonoid,
            BiFunction<BuildIntermediate, BuildSpec, BuildIntermediate> parentAndSpecToIntermediate
    ) {
        final List<BuildSpec> parents = buildGraph.getParents(rootSpec)
        if (parents.size() == 0) {
            parentAndSpecToIntermediate.apply(buildIntermediateMonoid.zero, rootSpec)
        } else {
            final List<BuildIntermediate> parentIntermediates = parents.collect {
                reduceWithParents(it, buildGraph, buildIntermediateMonoid, parentAndSpecToIntermediate)
            }
            final BuildIntermediate parentsReduced = parentIntermediates.inject(buildIntermediateMonoid.zero)
                    { acc, bi ->
                        buildIntermediateMonoid.concat.apply(acc, bi)
                    }
            parentAndSpecToIntermediate.apply(parentsReduced, rootSpec)
        }
    }

    static Collection<Build> getBuilds(Collection<BuildSpec> buildSpecs) {
        logger.trace(enter, 'buildSpecs: {}', buildSpecs)
        def graph = new BuildGraph(buildSpecs)
        def intermediates = buildSpecs.findResults {
            if (it.isAbstract) {
                null
            } else {
                reduceWithParents(it, graph, BuildMonoids.buildIntermediateMonoid)
                        { parent, spec ->
                            specToIntermediate(spec, parent)
                        }
            }
        }
        def builds = intermediates.collect { intermediateToBuild(it) }
        logger.trace(exit, 'builds: {}', builds)
        builds
    }

    private BuildSpecUtil() {}

}
