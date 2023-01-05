package com.jessebrault.ssg.specialpage

import groovy.transform.Canonical
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@Canonical
@TupleConstructor(defaults = false)
@NullCheck
class SpecialPage {
    String text
    String path
    SpecialPageType type
}
