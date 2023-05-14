package com.jessebrault.ssg.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.file.Path

import static org.junit.jupiter.api.Assertions.assertEquals

final class FileAssertions {

    private static final Logger logger = LoggerFactory.getLogger(FileAssertions)

    private static Map<String, Object> fileToMap(File file) {
        [
                type: file.isDirectory() ? 'directory' : (file.isFile() ? 'file' : null),
                text: file.file ? file.text : null
        ]
    }

    static void assertFileStructureAndContents(
            File expectedBaseDir,
            File actualBaseDir
    ) {
        final Map<Path, File> expectedPathsAndFiles = [:]
        def expectedBasePath = expectedBaseDir.toPath()
        expectedBaseDir.eachFileRecurse {
            def relativePath = expectedBasePath.relativize(it.toPath())
            expectedPathsAndFiles[relativePath] = it
        }
        logger.debug('expectedPaths: {}', expectedPathsAndFiles.keySet())

        expectedPathsAndFiles.forEach { relativePath, expectedFile ->
            def actualFile = new File(actualBaseDir, relativePath.toString())
            logger.debug(
                    'relativePath: {}, expectedFile: {}, actualFile: {}',
                    relativePath,
                    fileToMap(expectedFile),
                    fileToMap(actualFile)
            )
            assertEquals(expectedFile.directory, actualFile.directory)
            assertEquals(expectedFile.file, actualFile.file)
            if (expectedFile.file) {
                assertEquals(expectedFile.text, actualFile.text)
            }
        }
    }

    private FileAssertions() {}

}
