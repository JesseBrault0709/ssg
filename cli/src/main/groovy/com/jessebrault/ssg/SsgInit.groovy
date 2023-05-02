package com.jessebrault.ssg

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import picocli.CommandLine

import static com.jessebrault.ssg.util.ResourceUtil.copyResourceToFile

@CommandLine.Command(
        name = 'init',
        mixinStandardHelpOptions = true,
        description = 'Generates a blank project, optionally with some basic files.'
)
final class SsgInit extends AbstractSubCommand {

    static void init(File targetDir, boolean meaty) {
        new FileTreeBuilder(targetDir).with {
            dir('texts') {
                if (meaty) {
                    file('hello.md').tap {
                        copyResourceToFile('hello.md', it)
                    }
                }
            }
            dir('pages') {
                if (meaty) {
                    file('page.gsp').tap {
                        copyResourceToFile('page.gsp', it)
                    }
                }
            }
            dir('templates') {
                if (meaty) {
                    file('hello.gsp').tap {
                        copyResourceToFile('hello.gsp', it)
                    }
                }
            }
            dir('parts') {
                if (meaty) {
                    file('head.gsp').tap {
                        copyResourceToFile('head.gsp', it)
                    }
                }
            }

            if (meaty) {
                file('ssgBuilds.groovy').tap {
                    copyResourceToFile('ssgBuilds.groovy', it)
                }
            } else {
                file('ssgBuilds.groovy').tap {
                    copyResourceToFile('ssgBuildsBasic.groovy', it)
                }
            }
        }
    }

    private static final Logger logger = LogManager.getLogger(SsgInit)

    @CommandLine.Option(names = ['-m', '--meaty'], description = 'Include some basic files in the generated project.')
    boolean meaty

    @CommandLine.Option(names = '--targetDir', description = 'The directory in which to generate the project')
    File target = new File('.')

    @Override
    protected Integer doSubCommand() {
        logger.traceEntry()
        init(this.target, this.meaty)
        logger.traceExit(0)
    }

}
