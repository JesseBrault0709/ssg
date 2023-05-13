package com.jessebrault.ssg.util

import org.jetbrains.annotations.ApiStatus

import java.util.function.BinaryOperator

@ApiStatus.Experimental
interface Semigroup<T> {
    BinaryOperator<T> getConcat()
}