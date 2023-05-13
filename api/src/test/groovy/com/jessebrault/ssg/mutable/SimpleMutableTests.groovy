package com.jessebrault.ssg.mutable


import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertThrows
import static org.junit.jupiter.api.Assertions.assertTrue

final class SimpleMutableTests {

    @Test
    void set() {
        final Mutable<String> p = Mutables.getEmpty()
        p.set('test')
        assertEquals('test', p.get())
    }

    @Test
    void unset() {
        final Mutable<String> p = Mutables.getEmpty()
        p.set('test')
        p.unset()
        assertThrows(NullPointerException, p.&get)
    }

    @Test
    void mapInPlace() {
        final Mutable<String> p = Mutables.getEmpty()
        p.set('abc')
        p.mapInPlace { it + 'def' }
        assertEquals('abcdef', p.get())
    }

    @Test
    void filterInPlace() {
        final Mutable<String> p = Mutables.getEmpty()
        p.set('abc')
        p.filterInPlace { it.startsWith('z') }
        assertTrue(p.isEmpty())
    }

    @Test
    void matchEmpty() {
        final Mutable<String> p = Mutables.getEmpty()
        def matched = p.match(
                () -> 'a',
                { it + 'z' }
        )
        assertEquals('a', matched)
    }

    @Test
    void matchPresent() {
        final Mutable<String> p = Mutables.getEmpty()
        p.set('a')
        def matched = p.match(
                () -> 'a',
                {it + 'z' }
        )
        assertEquals('az', matched)
    }

}
