package com.jessebrault.ssg.util

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor
import org.jetbrains.annotations.Nullable

@TupleConstructor
@EqualsAndHashCode
final class Diagnostic {

    final String message
    final @Nullable Exception exception

    @Override
    String toString() {
        if (this.exception != null) {
            "Diagnostic(message: $message, exception: $exception.class.name)"
        } else {
            "Diagnostic(message: $message)"
        }
    }

}
