package com.jessebrault.ssg.util

import org.junit.jupiter.api.Test

import static ResourceUtil.copyResourceToFile
import static ResourceUtil.copyResourceToWriter
import static org.junit.jupiter.api.Assertions.assertEquals

final class ResourceUtilTests {

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
