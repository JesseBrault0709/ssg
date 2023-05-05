package com.jessebrault.ssg.util

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

import java.util.function.BinaryOperator
import java.util.function.Supplier

@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class Monoid<T> {
    final BinaryOperator<T> concat
    final Supplier<T> empty
}
