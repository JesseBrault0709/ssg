package com.jessebrault.ssg

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

final class SiteSpecTests {

    @Test
    void ifName0BlankThenName1() {
        def ss0 = new SiteSpec('', '')
        def ss1 = new SiteSpec('test', '')
        def sum = ss0 + ss1
        assertEquals('test', sum.name)
    }

    @Test
    void ifName0NotBlankThenName0() {
        def ss0 = new SiteSpec('ss0', '')
        def ss1 = new SiteSpec('ss1', '')
        def sum = ss0 + ss1
        assertEquals('ss0', sum.name)
    }

    @Test
    void ifBaseUrl0BlankThenBaseUrl1() {
        def ss0 = new SiteSpec('', '')
        def ss1 = new SiteSpec('', 'test')
        def sum = ss0 + ss1
        assertEquals('test', sum.baseUrl)
    }

    @Test
    void ifBaseUrl0NotBlankThenBaseUrl0() {
        def ss0 = new SiteSpec('', 'ss0')
        def ss1 = new SiteSpec('', 'ss1')
        def sum = ss0 + ss1
        assertEquals('ss0', sum.baseUrl)
    }

}
