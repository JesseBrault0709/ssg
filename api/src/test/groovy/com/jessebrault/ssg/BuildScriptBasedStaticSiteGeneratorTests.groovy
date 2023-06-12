package com.jessebrault.ssg

import com.jessebrault.ssg.buildscript.BuildScriptBase
import com.jessebrault.ssg.buildscript.BuildScriptConfiguratorFactory
import com.jessebrault.ssg.util.ResourceUtil
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.function.Consumer

import static com.jessebrault.ssg.util.FileAssertions.assertFileStructureAndContents
import static org.junit.jupiter.api.Assertions.assertTrue

final class BuildScriptBasedStaticSiteGeneratorTests {

    private static final Logger logger = LoggerFactory.getLogger(BuildScriptBasedStaticSiteGeneratorTests)

    @Test
    void buildWithOneTextAndTemplate() {
        def sourceDir = File.createTempDir()

        def buildScript = new File(sourceDir, 'build.groovy')
        ResourceUtil.copyResourceToFile('oneTextAndTemplate.groovy', buildScript)

        new FileTreeBuilder(sourceDir).tap {
            dir('texts') {
                file('hello.md', '---\ntemplate: hello.gsp\n---\nHello, World!')
            }
            dir('templates') {
                file('hello.gsp', '<%= text.render() %>')
            }
        }

        def tmpDir = File.createTempDir()
        def engine = new GroovyScriptEngine([sourceDir.toURI().toURL(), tmpDir.toURI().toURL()] as URL[])
        def ssg = new BuildScriptBasedStaticSiteGenerator(engine, [new BuildScriptConfiguratorFactory() {

            @Override
            Consumer<BuildScriptBase> get() {
                return { }
            }

        }], buildScript, [sourceDir: sourceDir, tmpDir: tmpDir, engine: engine])
        assertTrue(ssg.doBuild('test') {
            it.each { logger.error(it.toString()) }
        })

        def expectedBase = File.createTempDir()
        new File(expectedBase, 'hello.html').tap {
            ResourceUtil.copyResourceToFile('outputs/hello.html', it)
        }

        assertFileStructureAndContents(expectedBase, new File(sourceDir, 'build'))
    }

}
