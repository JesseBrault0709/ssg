package com.jessebrault.ssg.util

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class RelativePathHandlerTests {

    @Test
    void stripsExtension() {
        def stripped = new RelativePathHandler('hello.txt').getWithoutExtension()
        assertEquals('hello', stripped)
    }

}
