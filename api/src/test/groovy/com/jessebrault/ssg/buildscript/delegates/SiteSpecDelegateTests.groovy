package com.jessebrault.ssg.buildscript.delegates

import com.jessebrault.ssg.SiteSpec
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertThrows

final class SiteSpecDelegateTests {

    @Test
    void nullNotAllowed() {
        def d = new SiteSpecDelegate(SiteSpec.DEFAULT_MONOID)
        assertThrows(NullPointerException, {
            d.name = null
        })
    }

}
