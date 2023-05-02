package com.jessebrault.ssg

import com.jessebrault.ssg.util.ResourceUtil
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

final class StaticSiteGeneratorCliIntegrationTests {

    @Test
    void meatyInitAndBuild() {
        def tempDir = File.createTempDir()
        SsgInit.init(tempDir, true)

        def ssgBuild = new SsgBuild().tap {
            it.cli = new StaticSiteGeneratorCli().tap {
                it.logLevel = new StaticSiteGeneratorCli.LogLevel().tap {
                    it.trace = true
                }
            }
            it.baseDir = tempDir
            it.requestedBuilds = ['production']
        }
        assertEquals(0, ssgBuild.call())

        def buildDir = new File(tempDir, 'build')
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
        assertEquals(expectedPage, pageOutputFile)
    }

}
