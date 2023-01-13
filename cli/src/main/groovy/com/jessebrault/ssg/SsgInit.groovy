package com.jessebrault.ssg

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import picocli.CommandLine

@CommandLine.Command(
        name = 'init',
        mixinStandardHelpOptions = true,
        description = 'Generates a blank project, optionally with some basic files.'
)
class SsgInit extends AbstractSubCommand {

    private static final Logger logger = LogManager.getLogger(SsgInit)

    @CommandLine.Option(names = ['-s', '--skeleton'], description = 'Include some basic files in the generated project.')
    boolean withSkeletonFiles

    @Override
    Integer doSubCommand() {
        logger.traceEntry()
        new FileTreeBuilder().with {
            // Generate dirs
            dir('texts') {
                if (this.withSkeletonFiles) {
                    file('hello.md', this.getClass().getResource('/hello.md').text)
                }
            }
            dir('templates') {
                if (this.withSkeletonFiles) {
                    file('hello.gsp', this.getClass().getResource('/hello.gsp').text)
                }
            }
            dir('parts') {
                if (this.withSkeletonFiles) {
                    file('head.gsp', this.getClass().getResource('/head.gsp').text)
                }
            }
            dir('specialPages') {
                if (this.withSkeletonFiles) {
                    file('specialPage.gsp', this.getClass().getResource('/specialPage.gsp').text)
                }
            }

            // Generate ssgBuilds.groovy
            if (this.withSkeletonFiles) {
                file('ssgBuilds.groovy', this.getClass().getResource('/ssgBuilds.groovy').text)
            } else {
                file('ssgBuilds.groovy', this.getClass().getResource('/ssgBuildsBasic.groovy').text)
            }
        }
        logger.traceExit(0)
    }
}
