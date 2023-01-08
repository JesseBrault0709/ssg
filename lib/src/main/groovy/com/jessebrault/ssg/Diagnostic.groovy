package com.jessebrault.ssg

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

@TupleConstructor
@EqualsAndHashCode
class Diagnostic {

    String message
    Exception exception

    @Override
    String toString() {
        "Diagnostic(message: ${ this.message }, exception: ${ this.exception })"
    }

}
