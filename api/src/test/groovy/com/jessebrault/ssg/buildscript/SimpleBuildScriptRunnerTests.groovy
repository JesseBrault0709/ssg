package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.SiteSpec
import groovy.transform.NullCheck
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

import java.util.function.Consumer
import java.util.function.Function

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.mockito.Mockito.verify

@NullCheck
@ExtendWith(MockitoExtension)
final class SimpleBuildScriptRunnerTests {

    private final BuildScriptRunner runner = new SimpleBuildScriptRunner()

    /**
     * Must be non-static, otherwise Groovy gets confused inside the Closures.
     *
     * TODO: use the FileUtil.copyResourceToWriter method
     */
    @SuppressWarnings('GrMethodMayBeStatic')
    private void copyLocalResourceToWriter(String name, Writer target) {
        SimpleBuildScriptRunnerTests.getResourceAsStream(name).withReader {
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

    @Test
    void simpleScript() {
        def baseDir = this.setupScripts(['simple.groovy'])
        def builds = this.runner.runBuildScript('simple.groovy', baseDir.toURI().toURL())
        assertEquals(1, builds.size())
        assertEquals('test', builds[0].name)
    }

    @Test
    void testImport() {
        def baseDir = this.setupScripts(['testImport.groovy', 'TestHtmlTask.groovy'])
        def builds = this.runner.runBuildScript('testImport.groovy', baseDir.toURI().toURL())
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
        def builds = this.runner.runBuildScript(
                'buildSrcTest.groovy',
                baseDir.toURI().toURL(),
                [new File(baseDir, 'buildSrc').toURI().toURL()]
        )
        assertEquals(1, builds.size())
        assertEquals('test', builds[0].name)
    }

    @Test
    void withBinding(@Mock Consumer<String> stringConsumer) {
        def baseDir = this.setupScripts(['withBinding.groovy'])
        this.runner.runBuildScript(
                'withBinding.groovy',
                baseDir.toURI().toURL(),
                [],
                [stringConsumer: stringConsumer]
        )
        verify(stringConsumer).accept('test')
    }

    @Test
    void customScript() {
        def result = this.runner.runBuildScript {
            build(name: 'test') { }
        }
        assertEquals(1, result.size())
        assertEquals('test', result[0].name)
    }

    @Test
    void oneBuildOutputDirWithFunction(@Mock Function<Build, OutputDir> mockOutputDirFunction) {
        def r = this.runner.runBuildScript {
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
        def r = this.runner.runBuildScript {
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
        def r = this.runner.runBuildScript {
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
        def r = this.runner.runBuildScript {
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
        def r = this.runner.runBuildScript {
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
