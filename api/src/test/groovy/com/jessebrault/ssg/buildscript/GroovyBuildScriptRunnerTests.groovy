package com.jessebrault.ssg.buildscript

import groovy.transform.NullCheck
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@NullCheck
final class GroovyBuildScriptRunnerTests {

    /**
     * Must be non-static, otherwise Groovy gets confused inside the Closures.
     */
    @SuppressWarnings('GrMethodMayBeStatic')
    private void copyLocalResourceToWriter(String name, Writer target) {
        GroovyBuildScriptRunnerTests.getResourceAsStream(name).withReader {
            it.transferTo(target)
        }
    }

    /**
     * Must be non-static, otherwise Groovy gets confused inside the Closures.
     */
    @SuppressWarnings('GrMethodMayBeStatic')
    private File setupScriptEnvironment(Collection<String> resourceNames) {
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
        def baseDir = this.setupScriptEnvironment(['simple.groovy'])
        def runner = new GroovyBuildScriptRunner()
        def builds = runner.runBuildScript('simple.groovy', baseDir.toURI().toURL(), [])
        assertEquals(1, builds.size())
        assertEquals('test', builds[0].name)
    }

}
