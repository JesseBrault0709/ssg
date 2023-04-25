package com.jessebrault.ssg.util

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

@TupleConstructor
@EqualsAndHashCode
final class Diagnostic {

    String message
    Exception exception

    @Override
    String toString() {
        "Diagnostic(message: ${ this.message }, exception: ${ this.exception })"
    }

}
