package com.jessebrault.ssg.buildscript

import groovy.transform.PackageScope
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DirectedAcyclicGraph
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.MarkerFactory

@PackageScope
final class BuildGraphUtil {

    private static final Logger logger = LoggerFactory.getLogger(BuildGraphUtil)
    private static final Marker enter = MarkerFactory.getMarker('ENTER')
    private static final Marker exit = MarkerFactory.getMarker('EXIT')

    private static void addParentEdges(
            Graph<BuildSpec, DefaultEdge> graph,
            BuildSpec spec,
            Collection<BuildSpec> allBuildSpecs
    ) {
        logger.trace(enter, 'graph: {}, spec: {}', graph, spec)
        if (!graph.containsVertex(spec)) {
            throw new IllegalStateException("given spec is not in the graph")
        }
        if (spec.extending.present) {
            def parent = allBuildSpecs.find { it.name == spec.extending.buildName }
            if (parent == null) {
                throw new IllegalStateException("no such parent/extends from build: ${ spec.extending.buildName }")
            }
            if (!graph.containsVertex(parent)) {
                graph.addVertex(parent)
            }
            graph.addEdge(parent, spec)
            addParentEdges(graph, parent, allBuildSpecs)
        }
        logger.trace(exit, '')
    }

    static Graph<BuildSpec, DefaultEdge> getDependencyGraph(Collection<BuildSpec> buildSpecs) {
        logger.trace(enter, '')
        final Graph<BuildSpec, DefaultEdge> graph = new DirectedAcyclicGraph<>(DefaultEdge)
        buildSpecs.each {
            if (!graph.containsVertex(it)) {
                graph.addVertex(it)
            }
            addParentEdges(graph, it, buildSpecs)
        }
        logger.trace(exit, 'graph: {}', graph)
        graph
    }

    static Collection<BuildSpec> getAncestors(BuildSpec child, Graph<BuildSpec, DefaultEdge> graph) {
        logger.trace(enter, 'child: {}, graph: {}', child, graph)
        if (child.extending.isEmpty()) {
            def r = [] as Collection<BuildSpec>
            logger.trace(exit, 'r: {}', r)
            r
        } else {
            // use incoming to get edges pointing to child
            def incomingEdges = graph.incomingEdgesOf(child)
            if (incomingEdges.size() == 0) {
                throw new IllegalArgumentException("child does not have an edge to its parent")
            }
            if (incomingEdges.size() > 1) {
                throw new IllegalArgumentException("child has more than one parent")
            }
            def parent = graph.getEdgeSource(incomingEdges[0])
            def r = getAncestors(parent, graph) + [parent] // needs to be 'oldest' -> 'youngest'
            logger.trace(exit, 'r: {}', r)
            r
        }
    }

    private BuildGraphUtil() {}

}
