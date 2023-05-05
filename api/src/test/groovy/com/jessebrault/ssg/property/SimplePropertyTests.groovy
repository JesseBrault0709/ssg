package com.jessebrault.ssg.property

import com.jessebrault.ssg.buildscript.domain.MutableSiteSpec
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

final class SimplePropertyTests {

    @Test
    void merge() {
        def p = Properties.get(MutableSiteSpec.MONOID)
        p.merge {
            name.set('Hello')
        }
        p.map {
            it.name.map { it + ', World!' }
            it
        }
        def ms = p.get()
        assertEquals('Hello, World!', ms.name.get())
    }

}
