package com.jessebrault.ssg.buildscript

import groovy.transform.NullCheck
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

import java.util.function.Consumer

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.mockito.Mockito.verify

@NullCheck
@ExtendWith(MockitoExtension)
final class SimpleBuildScriptRunnerTests {

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
        def runner = new SimpleBuildScriptRunner()
        def builds = runner.runBuildScript('simple.groovy', baseDir.toURI().toURL())
        assertEquals(1, builds.size())
        assertEquals('test', builds[0].name)
    }

    @Test
    void testImport() {
        def baseDir = this.setupScripts(['testImport.groovy', 'TestHtmlTask.groovy'])
        def runner = new SimpleBuildScriptRunner()
        def builds = runner.runBuildScript('testImport.groovy', baseDir.toURI().toURL())
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
        def runner = new SimpleBuildScriptRunner()
        def builds = runner.runBuildScript(
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
        def runner = new SimpleBuildScriptRunner()
        runner.runBuildScript(
                'withBinding.groovy',
                baseDir.toURI().toURL(),
                [],
                [stringConsumer: stringConsumer]
        )
        verify(stringConsumer).accept('test')
    }

    @Test
    void customScript() {
        def runner = new SimpleBuildScriptRunner()
        def result = runner.runBuildScript {
            build('test') { }
        }
        assertEquals(1, result.size())
        assertEquals('test', result[0].name)
    }

}
