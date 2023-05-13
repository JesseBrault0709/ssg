package com.jessebrault.ssg.util

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.PackageScope
import groovy.transform.TupleConstructor
import org.jetbrains.annotations.ApiStatus

import java.util.function.BinaryOperator

@ApiStatus.Experimental
@PackageScope
@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class SimpleMonoid<T> implements Monoid<T> {
    final T zero
    final BinaryOperator<T> concat
}
