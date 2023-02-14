package com.jessebrault.ssg.util

import org.junit.jupiter.api.Test

import static com.jessebrault.ssg.util.ExtensionsUtil.stripExtension
import static com.jessebrault.ssg.util.ExtensionsUtil.getExtension
import static org.junit.jupiter.api.Assertions.assertEquals

class ExtensionsUtilTests {

    static class StripExtensionTests {

        @Test
        void simple() {
            assertEquals('test', stripExtension('test.txt'))
        }

        @Test
        void withSlashes() {
            assertEquals('test/test', stripExtension('test/test.txt'))
        }

        @Test
        void withMultipleExtensions() {
            assertEquals('test.txt', stripExtension('test.txt.html'))
        }

    }

    static class GetExtensionTests {

        @Test
        void simple() {
            assertEquals('.txt', getExtension('test.txt'))
        }

        @Test
        void withSlashes() {
            assertEquals('.txt', getExtension('test/test.txt'))
        }

        @Test
        void withMultipleExtensions() {
            assertEquals('.txt', getExtension('test.test.txt'))
        }

    }

}
