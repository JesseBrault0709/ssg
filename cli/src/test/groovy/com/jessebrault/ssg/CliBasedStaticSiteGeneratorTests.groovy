package com.jessebrault.ssg

import com.jessebrault.ssg.util.Diagnostic
import com.jessebrault.ssg.util.ResourceUtil
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

final class CliBasedStaticSiteGeneratorTests {

    @Test
    void meatyInitAndBuild() {
        def baseDir = File.createTempDir()
        SsgInit.init(baseDir, true)

        def tmpDir = File.createTempDir()
        def engine = new GroovyScriptEngine([
                new File(baseDir, 'buildSrc').toURI().toURL(),
                tmpDir.toURI().toURL()
        ] as URL[])
        def ssg = new CliBasedStaticSiteGenerator(
                baseDir,
                new File(baseDir, 'ssgBuilds.groovy'),
                tmpDir,
                engine,
                [:]
        )
        def diagnostics = [] as Collection<Diagnostic>
        assertTrue(ssg.doBuild('production', diagnostics.&addAll), {
            diagnostics.inject('') { acc, diagnostic ->
                acc + '\n' + diagnostic.message
            }
        })
        assertTrue(diagnostics.empty)

        def buildDir = new File(baseDir, 'production')
        assertTrue(buildDir.exists())
        assertTrue(buildDir.directory)

        def textOutputFile = new File(buildDir, 'hello.html')
        assertTrue(textOutputFile.exists())
        assertTrue(textOutputFile.file)
        def pageOutputFile = new File(buildDir, 'page.html')
        assertTrue(pageOutputFile.exists())
        assertTrue(pageOutputFile.file)

        def expectedText = ResourceUtil.loadResourceAsString('hello.html')
        def expectedPage = ResourceUtil.loadResourceAsString('page.html')
        assertEquals(expectedText, textOutputFile.text)
        assertEquals(expectedPage, pageOutputFile.text)
    }

}
