package com.jessebrault.ssg.part

import com.jessebrault.ssg.provider.CollectionProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.mockito.Mockito.mock

final class PartFilePartsProviderTests {

    private static final PartType gspPartType = new PartType(['.gsp'], mock(PartRenderer))

    private File partsDir
    private CollectionProvider<Part> partsProvider

    @BeforeEach
    void beforeEach() {
        this.partsDir = File.createTempDir()
        partsProvider = PartsProviders.of(this.partsDir, [gspPartType])
    }

    @Test
    void findsPart() {
        new FileTreeBuilder(this.partsDir).file('testPart.gsp') {
            write('Hello <%= name %>!')
        }

        def r = this.partsProvider.provide()
        assertEquals(1, r.size())
        def p0 = r[0]
        assertEquals('testPart.gsp', p0.path)
        assertEquals(gspPartType, p0.type)
        assertEquals('Hello <%= name %>!', p0.text)
    }

    @Test
    void findsNested() {
        new FileTreeBuilder(this.partsDir).dir('nested') {
            file('testPart.gsp') {
                write('Hello, World!')
            }
        }

        def r = this.partsProvider.provide()
        assertEquals(1, r.size())
        def p0 = r[0]
        assertEquals('nested/testPart.gsp', p0.path)
        assertEquals(gspPartType, p0.type)
        assertEquals('Hello, World!', p0.text)
    }

    @Test
    void ignoresUnsupportedFile() {
        new FileTreeBuilder(this.partsDir).file('.ignored') {
            write 'Ignored!'
        }

        def r = this.partsProvider.provide()
        assertEquals(0, r.size())
    }

}
