package com.jessebrault.ssg

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import picocli.CommandLine

@CommandLine.Command(
        hidden = true,
        name = 'watch',
        mixinStandardHelpOptions = true,
        description = 'Run in watch mode, rebuilding the project whenever files are created/updated/deleted.'
)
final class SsgWatch extends AbstractBuildCommand {

    private static final Logger logger = LogManager.getLogger(SsgWatch)

    @Override
    Integer doSubCommand() {
        logger.traceEntry()

//        // Setup watchService and watchKeys
//        def watchService = FileSystems.getDefault().newWatchService()
//        Map<WatchKey, Path> watchKeys = [:]
//
//        // Our Closure to register a directory path
//        def registerPath = { Path path ->
//            if (!Files.isDirectory(path)) {
//                throw new IllegalArgumentException('path must be a directory, given: ' + path)
//            }
//            logger.debug('registering dir with path: {}', path)
//            def watchKey = path.register(
//                    watchService,
//                    StandardWatchEventKinds.ENTRY_CREATE,
//                    StandardWatchEventKinds.ENTRY_DELETE,
//                    StandardWatchEventKinds.ENTRY_MODIFY
//            )
//            watchKeys[watchKey] = path
//            logger.debug('watchKeys: {}', watchKeys)
//        }
//
//        // Get all base watchableDirs
//        Collection<WithWatchableDir> watchableProviders = []
//        this.builds.each {
//            it.config.textProviders.each {
//                if (it instanceof WithWatchableDir) {
//                    watchableProviders << it
//                }
//            }
//            it.config.templatesProviders.each {
//                if (it instanceof WithWatchableDir) {
//                    watchableProviders << it
//                }
//            }
//            it.config.partsProviders.each {
//                if (it instanceof WithWatchableDir) {
//                    watchableProviders << it
//                }
//            }
//            it.config.specialPagesProviders.each {
//                if (it instanceof WithWatchableDir) {
//                    watchableProviders << it
//                }
//            }
//        }
//        // register them and their child directories using the Closure above
//        watchableProviders.each {
//            def baseDirFile = it.watchableDir
//            registerPath(baseDirFile.toPath())
//            baseDirFile.eachFile(FileType.DIRECTORIES) {
//                registerPath(it.toPath())
//            }
//        }
//
//        //noinspection GroovyInfiniteLoopStatement
//        while (true) {
//            def watchKey = watchService.take()
//            def path = watchKeys[watchKey]
//            if (path == null) {
//                logger.warn('unexpected watchKey: {}', watchKey)
//            } else {
//                watchKey.pollEvents().each {
//                    assert it instanceof WatchEvent<Path>
//                    def childName = it.context()
//                    def childPath = path.resolve(childName)
//                    if (it.kind() == StandardWatchEventKinds.ENTRY_CREATE && Files.isDirectory(childPath)) {
//                        registerPath(childPath)
//                    } else if (Files.isRegularFile(childPath)) {
//                        logger.debug('detected {} for regularFile with path {}', it.kind(), childPath)
//                        def t = new Thread({
//                            this.doBuild()
//                        })
//                        t.setName('workerThread')
//                        t.start()
//                    }
//                }
//            }
//            def valid = watchKey.reset()
//            if (!valid) {
//                def removedPath = watchKeys.remove(watchKey)
//                logger.debug('removed path: {}', removedPath)
//            }
//        }

        //noinspection GroovyUnreachableStatement
        logger.traceExit(0)
    }

}
