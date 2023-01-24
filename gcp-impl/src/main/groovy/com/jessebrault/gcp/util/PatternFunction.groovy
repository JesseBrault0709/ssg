package com.jessebrault.gcp.util

import groovy.transform.PackageScope
import groovy.transform.TupleConstructor

import java.util.function.Function
import java.util.regex.Pattern

@TupleConstructor(includeFields = true, defaults = false)
class PatternFunction implements Function<String, String> {

    protected final Pattern pattern

    @Override
    String apply(String s) {
        def matcher = this.pattern.matcher(s)
        matcher.find() ? matcher.group() : null
    }

    @Override
    String toString() {
        "PatternFunction(pattern: ${ this.pattern })"
    }

}
