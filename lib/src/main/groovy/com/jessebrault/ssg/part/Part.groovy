package com.jessebrault.ssg.part

import groovy.transform.Canonical
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@Canonical
@TupleConstructor(defaults = false)
@NullCheck
class Part {
    String name
    PartType type
    String text
}
