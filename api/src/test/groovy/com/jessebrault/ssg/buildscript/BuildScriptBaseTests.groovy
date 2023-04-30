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

    private static Collection<Build> scriptToBuilds(
            @DelegatesTo(value = BuildScriptBase, strategy = Closure.DELEGATE_FIRST)
            Closure<?> script
    ) {
        def s = new BuildScriptBase() {

            @Override
            Object run() {
                script.delegate = this
                script.resolveStrategy = Closure.DELEGATE_FIRST
                script()
            }

        }
        s.run()
        s.getBuilds()
    }

    @Test
    void oneBuildWithName0() {
        def r = scriptToBuilds {
            build('test') { }
        }
        assertEquals(1, r.size())
        def b0 = r[0]
        assertEquals('test', b0.name)
    }

    @Test
    void oneBuildWithName1() {
        def r = scriptToBuilds {
            build {
                name = 'test'
            }
        }
        assertEquals(1, r.size())
        def b0 = r[0]
        assertEquals('test', b0.name)
    }

    @Test
    void oneBuildOutputDirWithFunction(@Mock Function<Build, OutputDir> mockOutputDirFunction) {
        def r = scriptToBuilds {
            build {
                outputDirFunction = mockOutputDirFunction
            }
        }
        assertEquals(1, r.size())
        def b0 = r[0]
        assertEquals(mockOutputDirFunction, b0.outputDirFunction)
    }

    @Test
    void oneBuildOutputDirWithFile() {
        def f = new File('test')
        def r = scriptToBuilds {
            build {
                outputDir = f
            }
        }
        assertEquals(1, r.size())
        def b0 = r[0]
        assertEquals(f, b0.outputDirFunction.apply(b0) as File)
    }

    @Test
    void oneBuildOutputDirWithString() {
        def r = scriptToBuilds {
            build {
                outputDir = 'test'
            }
        }
        assertEquals(1, r.size())
        def b0 = r[0]
        assertEquals('test', b0.outputDirFunction.apply(b0) as String)
    }

    @Test
    void oneBuildSiteSpec() {
        def r = scriptToBuilds {
            build {
                siteSpec {
                    name = 'testSite'
                    baseUrl = 'https://testsite.com'
                }
            }
        }
        assertEquals(1, r.size())
        def b0 = r[0]
        assertEquals(new SiteSpec('testSite', 'https://testsite.com'), b0.siteSpec)
    }

    @Test
    void allBuildsProvidesSiteSpec() {
        def r = scriptToBuilds {
            allBuilds {
                siteSpec {
                    name = 'testSite'
                    baseUrl = 'https://testsite.com'
                }
            }
            build('test') { }
        }
        assertEquals(1, r.size())
        def b0 = r[0]
        assertEquals(new SiteSpec('testSite', 'https://testsite.com'), b0.siteSpec)
    }

    @Test
    void allBuildsSiteSpecOverwritten() {
        def r = scriptToBuilds {
            allBuilds {
                siteSpec {
                    name = 'no'
                }
            }
            build {
                siteSpec {
                    name = 'yes'
                }
            }
        }
        assertEquals(1, r.size())
        def b0 = r[0]
        assertEquals(new SiteSpec('yes', ''), b0.siteSpec)
    }

}
