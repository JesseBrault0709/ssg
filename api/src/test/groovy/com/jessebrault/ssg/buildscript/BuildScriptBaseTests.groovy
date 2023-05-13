package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.SiteSpec
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

import java.util.function.Function

import static org.junit.jupiter.api.Assertions.assertEquals

@ExtendWith(MockitoExtension)
final class BuildScriptBaseTests {

    private static Collection<BuildSpec> scriptToBuildSpecs(
            @DelegatesTo(value = BuildScriptBase, strategy = Closure.DELEGATE_FIRST)
            Closure<?> script
    ) {
        def base = new BuildScriptBase() {

            @Override
            Object run() {
                script.delegate = this
                script.resolveStrategy = Closure.DELEGATE_FIRST
                script()
            }

        }
        base.run()
        base.buildSpecs
    }

    @Deprecated
    private static Collection<Build> scriptToBuilds(
            @DelegatesTo(value = BuildScriptBase, strategy = Closure.DELEGATE_FIRST)
            Closure<?> script
    ) {
        BuildSpecUtil.getBuilds(scriptToBuildSpecs(script))
    }

    @Test
    void oneBuildWithName0() {
        def r = scriptToBuildSpecs {
            build(name: 'test') { }
        }
        assertEquals(1, r.size())
        def b0 = r[0]
        assertEquals('test', b0.name)
    }

    @Test
    void oneBuildWithName1() {
        def r = scriptToBuildSpecs {
            build(name: 'test') { }
        }
        assertEquals(1, r.size())
        def b0 = r[0]
        assertEquals('test', b0.name)
    }

    @Test
    void twoBuildsNotRelated() {
        def r = scriptToBuildSpecs {
            build(name: 'b0') { }
            build(name: 'b1') { }
        }
        assertEquals(2, r.size())
    }

    @Test
    void childParentBuild() {
        def r = scriptToBuildSpecs {
            build(name: 'child', extending: 'parent') { }
            build(name: 'parent') { }
        }
        assertEquals(2, r.size())
        assertEquals(
                [
                        BuildSpec.get(name: 'child', extending: BuildExtension.get('parent')),
                        BuildSpec.get(name: 'parent')
                ],
                r
        )
    }

    @Test
    void threeGenerations() {
        def r = scriptToBuildSpecs {
            build(name: 'child', extending: 'parent') { }
            build(name: 'parent', extending: 'grandparent') { }
            build(name: 'grandparent') { }
        }
        assertEquals(3, r.size())
        assertEquals(
                [
                        BuildSpec.get(name: 'child', extending: BuildExtension.get('parent')),
                        BuildSpec.get(name: 'parent', extending: BuildExtension.get('grandparent')),
                        BuildSpec.get(name: 'grandparent')
                ],
                r
        )
    }

    @Test
    void siblingsAndParent() {
        def r = scriptToBuildSpecs {
            build(name: 'child0', extending: 'parent') { }
            build(name: 'child1', extending: 'parent') { }
            build(name: 'parent') { }
        }
        assertEquals(3, r.size())
        assertEquals(
                [
                        BuildSpec.get(name: 'child0', extending: BuildExtension.get('parent')),
                        BuildSpec.get(name: 'child1', extending: BuildExtension.get('parent')),
                        BuildSpec.get(name: 'parent')
                ],
                r
        )
    }

}
