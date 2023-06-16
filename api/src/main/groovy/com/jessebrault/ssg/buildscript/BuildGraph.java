package com.jessebrault.ssg.buildscript;

import com.jessebrault.ssg.util.Monoid;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

public final class BuildGraph {

    private static void addAncestorEdges(
            Graph<BuildSpec, DefaultEdge> graph,
            BuildSpec buildSpec,
            Collection<BuildSpec> allBuildSpecs
    ) {
        if (!graph.containsVertex(buildSpec)) {
            throw new IllegalStateException("Given buildSpec " + buildSpec.getName() + " is not in the given graph.");
        }
        for (final var extendingName : buildSpec.getExtending()) {
            final var ancestor = allBuildSpecs.stream()
                    .filter(bs -> extendingName.equals(bs.getName()))
                    .findAny()
                    .orElseThrow(() -> new IllegalStateException(
                            "Could not find ancestor " + extendingName + " for buildSpec " + buildSpec.getName() + "."
                    ));
            graph.addEdge(ancestor, buildSpec);
            addAncestorEdges(graph, ancestor, allBuildSpecs);
        }
    }

    private final Graph<BuildSpec, DefaultEdge> graph;

    public BuildGraph(Collection<BuildSpec> buildSpecs) {
        this.graph = new DirectedAcyclicGraph<>(DefaultEdge.class);
        for (final var buildSpec : buildSpecs) {
            if (!this.graph.containsVertex(buildSpec)) {
                this.graph.addVertex(buildSpec);
            }
            addAncestorEdges(this.graph, buildSpec, buildSpecs);
        }
    }

    public List<BuildSpec> getParents(BuildSpec start) {
        return this.graph.incomingEdgesOf(start).stream()
                .map(this.graph::getEdgeSource)
                .toList();
    }

    @Deprecated(forRemoval = true)
    public BuildIntermediate toIntermediate(
            BuildSpec rootSpec,
            Monoid<BuildIntermediate> buildIntermediateMonoid,
            BiFunction<BuildIntermediate, BuildSpec, BuildIntermediate> parentAndSpecToIntermediate
    ) {
        final List<BuildSpec> parents = this.getParents(rootSpec);
        if (parents.size() == 0) {
            return parentAndSpecToIntermediate.apply(buildIntermediateMonoid.getZero(), rootSpec);
        } else {
            final List<BuildIntermediate> parentIntermediates = parents.stream()
                    .map(parent -> this.toIntermediate(parent, buildIntermediateMonoid, parentAndSpecToIntermediate))
                    .toList();
            final BuildIntermediate parentsReduced = parentIntermediates.stream()
                    .reduce(buildIntermediateMonoid.getZero(), (bi0, bi1) ->
                            buildIntermediateMonoid.getConcat().apply(bi0, bi1)
                    );
            return parentAndSpecToIntermediate.apply(parentsReduced, rootSpec);
        }
    }

}
