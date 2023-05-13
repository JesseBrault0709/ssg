package com.jessebrault.ssg.util

import java.util.function.BinaryOperator

final class Semigroups {

    static <T> Semigroup<T> of(BinaryOperator<T> concat) {
        new SimpleSemigroup<>(concat)
    }

    private Semigroups() {}

}
