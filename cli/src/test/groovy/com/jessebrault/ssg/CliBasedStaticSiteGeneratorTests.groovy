package com.jessebrault.ssg

import com.jessebrault.ssg.util.Diagnostic
import com.jessebrault.ssg.util.ResourceUtil
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

final class CliBasedStaticSiteGeneratorTests {

    @Test
    void meatyInitAndBuild() {
        def tempDir = File.createTempDir()
        SsgInit.init(tempDir, true)

        def ssg = new CliBasedStaticSiteGenerator(
                tempDir,
                new File('ssgBuilds.groovy'),
                [new File('buildSrc')],
                [:],
                this.class.classLoader,
                [new File(tempDir, 'buildSrc').toURI().toURL()]
        )
        def diagnostics = [] as Collection<Diagnostic>
        assertTrue(ssg.doBuild('production', diagnostics.&addAll))
        assertTrue(diagnostics.empty)

        def buildDir = new File(tempDir, 'production')
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
