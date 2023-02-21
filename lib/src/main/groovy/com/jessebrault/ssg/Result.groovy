package com.jessebrault.ssg

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false, includeFields = true)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class Result<T> {

    final Collection<Diagnostic> diagnostics
    private final T t

    boolean hasDiagnostics() {
        !this.diagnostics.isEmpty()
    }

    T get() {
        this.t
    }

    @Override
    String toString() {
        "Result(diagnostics: ${ this.diagnostics }, t: ${ this.t })"
    }

}
