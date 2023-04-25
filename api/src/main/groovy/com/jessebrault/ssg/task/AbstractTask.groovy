package com.jessebrault.ssg.task

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
abstract class AbstractTask implements Task {

    final String name

    @Override
    String toString() {
        "AbstractTask(name: ${ this.name })"
    }

}
