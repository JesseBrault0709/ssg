package com.jessebrault.ssg.util

import java.util.function.BiPredicate

final class Eqs {

    static <T> Eq<T> of(BiPredicate<T, T> areEqual) {
        new SimpleEq<>(areEqual)
    }

}
