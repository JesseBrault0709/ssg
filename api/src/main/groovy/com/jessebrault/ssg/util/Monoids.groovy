package com.jessebrault.ssg.util

import org.jetbrains.annotations.ApiStatus

import java.util.function.BinaryOperator

@ApiStatus.Experimental
final class Monoids {

    static <T> Monoid<T> of(T zero, BinaryOperator<T> concat) {
        new SimpleMonoid<>(zero, concat)
    }

    private Monoids() {}

}
