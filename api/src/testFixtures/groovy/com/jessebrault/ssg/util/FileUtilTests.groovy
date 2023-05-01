package com.jessebrault.ssg.util

import org.junit.jupiter.api.Test

import static com.jessebrault.ssg.util.FileUtil.assertFileStructureAndContents
import static com.jessebrault.ssg.util.FileUtil.copyResourceToFile
import static com.jessebrault.ssg.util.FileUtil.copyResourceToWriter
import static org.junit.jupiter.api.Assertions.assertEquals

final class FileUtilTests {

    @Test
    void sameStructureAndContentsTest() {
        def b0 = File.createTempDir()
        def b1 = File.createTempDir()
        [b0, b1].each {
            new FileTreeBuilder(it).tap {
                file('testFile', 'test content')
                dir('testDir') {
                    file('testNestedFile', 'test content')
                }
            }
        }
        assertFileStructureAndContents(b0, b1)
    }

    @Test
    void copyResourceToWriterTest() {
        def writer = new StringWriter()
        copyResourceToWriter('testResource.txt', writer)
        assertEquals('Hello, World!', writer.toString())
    }

    @Test
    void copyResourceToTargetFileTest() {
        def tempDir = File.createTempDir()
        def target = new File(tempDir, 'testResource.txt')
        copyResourceToFile('testResource.txt', target)
        assertEquals('Hello, World!', target.text)
    }

}
