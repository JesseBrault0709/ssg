package com.jessebrault.ssg.util

import java.util.function.BiPredicate

interface Eq<T> {
    BiPredicate<T, T> getAreEqual()
}