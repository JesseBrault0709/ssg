package com.jessebrault.ssg.part

import groovy.transform.Canonical
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@Canonical
@TupleConstructor(defaults = false)
@NullCheck
class PartType {
    Collection<String> extensions
    PartRenderer renderer
}
