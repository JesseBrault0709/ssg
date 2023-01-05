package com.jessebrault.ssg.part

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class PartFilePartsProviderTests {

    private static final PartType gspPartType = new PartType(['.gsp'], null)

    private File partsDir
    private PartsProvider partsProvider

    @BeforeEach
    void beforeEach() {
        this.partsDir = File.createTempDir()
        partsProvider = new PartFilePartsProvider([gspPartType], this.partsDir)
    }

    @Test
    void findsPart() {
        new FileTreeBuilder(this.partsDir).file('testPart.gsp') {
            write('Hello <%= name %>!')
        }

        def r = this.partsProvider.getParts()
        assertEquals(1, r.size())
        def p0 = r[0]
        assertEquals('testPart.gsp', p0.name)
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

        def r = this.partsProvider.getParts()
        assertEquals(1, r.size())
        def p0 = r[0]
        assertEquals('nested/testPart.gsp', p0.name)
        assertEquals(gspPartType, p0.type)
        assertEquals('Hello, World!', p0.text)
    }

    @Test
    void ignoresUnsupportedFile() {
        new FileTreeBuilder(this.partsDir).file('.ignored') {
            write 'Ignored!'
        }

        def r = this.partsProvider.getParts()
        assertEquals(0, r.size())
    }

}
