package com.jessebrault.ssg.buildscript

import groovy.transform.PackageScope
import org.junit.jupiter.api.Test

import static com.jessebrault.ssg.buildscript.BuildGraphUtil.getAncestors
import static com.jessebrault.ssg.buildscript.BuildGraphUtil.getDependencyGraph
import static org.junit.jupiter.api.Assertions.*

@PackageScope
final class BuildGraphUtilTests {

    @Test
    void oneBuildGraph() {
        def spec = BuildSpec.getEmpty()
        def graph = getDependencyGraph([spec])
        assertTrue(graph.containsVertex(spec))
        assertEquals(0, graph.edgesOf(spec).size())
    }

    @Test
    void twoBuildsNotConnectedGraph() {
        def spec0 = BuildSpec.getEmpty()
        def spec1 = BuildSpec.getEmpty()
        def graph = getDependencyGraph([spec0, spec1])
        [spec0, spec1].each {
            assertTrue(graph.containsVertex(it))
            assertEquals(0, graph.edgesOf(it).size())
        }
    }

    @Test
    void twoBuildsParentAndChildGraph() {
        def child = BuildSpec.get(name: 'child', extending: BuildExtension.get('parent'))
        def parent = BuildSpec.get(name: 'parent')
        def graph = getDependencyGraph([child, parent])
        [child, parent].each {
            assertTrue(graph.containsVertex(it))
        }
        assertEquals(1, graph.edgeSet().size())
        assertTrue(graph.containsEdge(parent, child))
    }

    @Test
    void threeBuildPyramidGraph() {
        def left = BuildSpec.get(name: 'left', extending: BuildExtension.get('parent'))
        def right = BuildSpec.get(name: 'right', extending: BuildExtension.get('parent'))
        def parent = BuildSpec.get(name: 'parent')
        def graph = getDependencyGraph([left, right, parent])
        [left, right, parent].each {
            assertTrue(graph.containsVertex(it))
        }
        assertEquals(2, graph.edgeSet().size())
        assertTrue(graph.containsEdge(parent, left))
        assertTrue(graph.containsEdge(parent, right))
    }

    @Test
    void noAncestors() {
        def spec = BuildSpec.getEmpty()
        def graph = getDependencyGraph([spec])
        def ancestors = getAncestors(spec, graph)
        assertEquals(0, ancestors.size())
    }

    @Test
    void oneAncestor() {
        def child = BuildSpec.get(name: 'child', extending: BuildExtension.get('parent'))
        def parent = BuildSpec.get(name: 'parent')
        def graph = getDependencyGraph([child, parent])
        def ancestors = getAncestors(child, graph)
        assertEquals(1, ancestors.size())
        assertIterableEquals([parent], ancestors)
    }

    @Test
    void pyramidNotConfusedBySibling() {
        def child = BuildSpec.get(name: 'child', extending: BuildExtension.get('parent'))
        def sibling = BuildSpec.get(name: 'sibling', extending: BuildExtension.get('parent'))
        def parent = BuildSpec.get(name: 'parent')
        def graph = getDependencyGraph([child, sibling, parent])
        def ancestorsOfChild = getAncestors(child, graph)
        assertEquals(1, ancestorsOfChild.size())
        assertIterableEquals([parent], ancestorsOfChild)
    }

    @Test
    void threeGenerationAncestors() {
        def child = BuildSpec.get(name: 'child', extending: BuildExtension.get('parent'))
        def parent = BuildSpec.get(name: 'parent', extending: BuildExtension.get('grandparent'))
        def grandparent = BuildSpec.get(name: 'grandparent')
        def graph = getDependencyGraph([child, parent, grandparent])
        def ancestorsOfChild = getAncestors(child, graph)
        assertEquals(2, ancestorsOfChild.size())
        assertIterableEquals([grandparent, parent], ancestorsOfChild)
    }

}
