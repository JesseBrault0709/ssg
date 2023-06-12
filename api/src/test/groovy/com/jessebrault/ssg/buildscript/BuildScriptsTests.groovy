package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.SiteSpec
import groovy.transform.NullCheck
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

import java.util.function.Consumer
import java.util.function.Function

import static com.jessebrault.ssg.buildscript.BuildScripts.runBuildScript
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.mockito.Mockito.verify

final class BuildScriptsTests {

    @NullCheck
    @ExtendWith(MockitoExtension)
    static final class ScriptFileTests {

        /**
         * Must be non-static, otherwise Groovy gets confused inside the Closures.
         *
         * TODO: use the FileUtil.copyResourceToWriter method
         */
        @SuppressWarnings('GrMethodMayBeStatic')
        private void copyLocalResourceToWriter(String name, Writer target) {
            BuildScriptsTests.getResourceAsStream(name).withReader {
                it.transferTo(target)
            }
        }

        /**
         * Must be non-static, otherwise Groovy gets confused inside the Closures.
         */
        @SuppressWarnings('GrMethodMayBeStatic')
        private File setupScripts(Collection<String> resourceNames) {
            def tempDir = File.createTempDir()
            new FileTreeBuilder(tempDir).tap {
                resourceNames.each { String resourceName ->
                    file(resourceName).withWriter {
                        this.copyLocalResourceToWriter(resourceName, it)
                    }
                }
            }
            tempDir
        }

        private static Collection<Build> getBuilds(
                String scriptName,
                Collection<URL> urls,
                Map<String, Object> binding = [:],
                Consumer<BuildScriptBase> configureBase = { }
        ) {
            runBuildScript(scriptName, new GroovyScriptEngine(urls as URL[]), binding, configureBase)
        }

        @Test
        void simpleScript() {
            def baseDir = this.setupScripts(['simple.groovy'])
            def builds = getBuilds('simple.groovy', [baseDir.toURI().toURL()])
            assertEquals(1, builds.size())
            assertEquals('test', builds[0].name)
        }

        @Test
        void testImport() {
            def baseDir = this.setupScripts(['testImport.groovy', 'TestHtmlTask.groovy'])
            def builds = getBuilds('testImport.groovy', [baseDir.toURI().toURL()])
            assertEquals(1, builds.size())
            assertEquals('test', builds[0].name)
        }

        @Test
        void buildSrcTest() {
            def baseDir = File.createTempDir()
            new FileTreeBuilder(baseDir).tap {
                file('buildSrcTest.groovy').withWriter {
                    this.copyLocalResourceToWriter('buildSrcTest.groovy', it)
                }
                dir('buildSrc') {
                    file('AnotherTask.groovy').withWriter {
                        this.copyLocalResourceToWriter('buildSrc/AnotherTask.groovy', it)
                    }
                }
            }
            def builds = getBuilds(
                    'buildSrcTest.groovy',
                    [
                            baseDir.toURI().toURL(),
                            new File(baseDir, 'buildSrc').toURI().toURL()
                    ]
            )
            assertEquals(1, builds.size())
            assertEquals('test', builds[0].name)
        }

        @Test
        void withBinding(@Mock Consumer<String> stringConsumer) {
            def baseDir = this.setupScripts(['withBinding.groovy'])
            def engine = new GroovyScriptEngine([baseDir.toURI().toURL()] as URL[])
            runBuildScript('withBinding.groovy', engine, [stringConsumer: stringConsumer])
            verify(stringConsumer).accept('test')
        }

    }

    @ExtendWith(MockitoExtension)
    static final class ClosureScriptTests {

        @Test
        void simple() {
            def result = runBuildScript {
                build(name: 'test') { }
            }
            assertEquals(1, result.size())
            assertEquals('test', result[0].name)
        }

        @Test
        void oneBuildOutputDirWithFunction(@Mock Function<Build, OutputDir> mockOutputDirFunction) {
            def r = runBuildScript {
                build(name: 'test') {
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
            def r = runBuildScript {
                build(name: 'test') {
                    outputDir = f
                }
            }
            assertEquals(1, r.size())
            def b0 = r[0]
            assertEquals(f, b0.outputDirFunction.apply(b0) as File)
        }

        @Test
        void oneBuildOutputDirWithString() {
            def r = runBuildScript {
                build(name: 'test') {
                    outputDir = 'test'
                }
            }
            assertEquals(1, r.size())
            def b0 = r[0]
            assertEquals('test', b0.outputDirFunction.apply(b0) as String)
        }

        @Test
        void oneBuildSiteSpec() {
            def r = runBuildScript {
                build(name: 'test') {
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
        void oneBuildWithAbstractParent() {
            def r = runBuildScript {
                abstractBuild(name: 'parent') {
                    siteSpec {
                        name = 'Test Site'
                        baseUrl = 'https://test.com'
                    }
                }

                build(name: 'child', extending: 'parent') {
                    siteSpec { base ->
                        baseUrl = base.baseUrl + '/child'
                    }
                }
            }

            assertEquals(1, r.size())
            def b0 = r[0]
            assertEquals(new SiteSpec('Test Site', 'https://test.com/child'), b0.siteSpec)
        }

    }

}
