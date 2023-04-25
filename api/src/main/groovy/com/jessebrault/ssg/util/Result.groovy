package com.jessebrault.ssg.util

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

import static java.util.Objects.requireNonNull

@EqualsAndHashCode(includeFields = true)
final class Result<T> {

    static <T> Result<T> of(T t) {
        new Result<>([], requireNonNull(t))
    }

    static <T> Result<T> of(Collection<Diagnostic> diagnostics, T t) {
        new Result<T>(requireNonNull(diagnostics), requireNonNull(t))
    }

    static <T> Result<T> ofDiagnostics(Collection<Diagnostic> diagnostics) {
        new Result<T>(requireNonNull(diagnostics), null)
    }

    final Collection<Diagnostic> diagnostics
    private final T t

    private Result(Collection<Diagnostic> diagnostics, T t) {
        this.diagnostics = requireNonNull(diagnostics)
        this.t = t
    }

    boolean hasDiagnostics() {
        !this.diagnostics.isEmpty()
    }

    T get() {
        requireNonNull(this.t)
    }

    @Override
    String toString() {
        "Result(diagnostics: ${ this.diagnostics }, t: ${ this.t })"
    }

}
