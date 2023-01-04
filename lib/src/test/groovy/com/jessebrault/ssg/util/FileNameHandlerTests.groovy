package com.jessebrault.ssg.util

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class FileNameHandlerTests {

    private final FileNameHandler fileNameHandler = new FileNameHandler()

    @Test
    void getsCorrectExtension() {
        def file = new File('hello.txt')
        def extension = new FileNameHandler(file).getExtension()
        assertEquals('.txt', extension)
    }

}
