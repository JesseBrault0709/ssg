package com.jessebrault.ssg.util

import groowt.util.fp.property.Property
import groowt.util.fp.provider.Provider

import static java.util.Objects.requireNonNull

final class ObjectUtil {

    static <T> T requireType(Class<T> type, Object t) {
        type.cast(requireNonNull(t))
    }

    static String requireString(s) {
        requireNonNull(s) as String
    }

    static File requireFile(f) {
        requireNonNull(f) as File
    }

    static Map requireMap(m) {
        requireNonNull(m) as Map
    }

    static Set requireSet(s) {
        requireNonNull(s) as Set
    }

    static Property requireProperty(p) {
        requireNonNull(p) as Property
    }

    static Provider requireProvider(p) {
        requireNonNull(p) as Provider
    }

    private ObjectUtil() {}

}
