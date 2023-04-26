package com.jessebrault.ssg.util

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertNull

final class ExtensionUtilTests {

    static class StripExtensionTests {

        @Test
        void simple() {
            assertEquals('test', ExtensionUtil.stripExtension('test.txt'))
        }

        @Test
        void withSlashes() {
            assertEquals('test/test', ExtensionUtil.stripExtension('test/test.txt'))
        }

        @Test
        void withMultipleExtensions() {
            assertEquals('test.txt', ExtensionUtil.stripExtension('test.txt.html'))
        }

    }

    static class GetExtensionTests {

        @Test
        void simple() {
            assertEquals('.txt', ExtensionUtil.getExtension('test.txt'))
        }

        @Test
        void withSlashes() {
            assertEquals('.txt', ExtensionUtil.getExtension('test/test.txt'))
        }

        @Test
        void withMultipleExtensions() {
            assertEquals('.txt', ExtensionUtil.getExtension('test.test.txt'))
        }

        @Test
        void noExtensionReturnsNull() {
            assertNull(ExtensionUtil.getExtension('NO_EXTENSION'))
        }

        @Test
        void dotFileReturnsNull() {
            assertNull(ExtensionUtil.getExtension('.dot_file'))
        }

    }

}
