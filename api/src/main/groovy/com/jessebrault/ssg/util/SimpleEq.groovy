package com.jessebrault.ssg.util

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.PackageScope
import groovy.transform.TupleConstructor

import java.util.function.BiPredicate

@PackageScope
@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class SimpleEq<T> implements Eq<T> {
    final BiPredicate<T, T> areEqual
}
