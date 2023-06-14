package com.jessebrault.ssg

import com.jessebrault.ssg.util.ResourceUtil
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

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

        def ssg = new BuildScriptBasedStaticSiteGenerator([sourceDir.toURI().toURL()], buildScript)

        assertTrue(
                ssg.doBuild(
                        'test',
                        [],
                        [sourceDir: sourceDir, gclSupplier: ssg::getBuildScriptClassLoader]
                ) {
                    it.each { logger.error(it.toString()) }
                }
        )

        def expectedBase = File.createTempDir()
        new File(expectedBase, 'hello.html').tap {
            ResourceUtil.copyResourceToFile('outputs/hello.html', it)
        }

        assertFileStructureAndContents(expectedBase, new File(sourceDir, 'build'))
    }

}
