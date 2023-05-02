package com.jessebrault.ssg.property

import com.jessebrault.ssg.provider.Provider

import java.util.function.Function
import java.util.function.UnaryOperator

interface Property<T> extends Provider<T> {

    void set(T t)
    void set(Provider<T> provider)
    void unset()

    default void leftShift(T t) {
        this.set(t)
    }

    default void leftShift(Provider<T> provider) {
        this.set(provider)
    }

    T getConvention()
    void setConvention(T t)
    void setConvention(Provider<T> provider)
    void unsetConvention()

    void map(UnaryOperator<T> operator)
    void flatMap(Function<T, Provider<T>> function)

    default void rightShift(UnaryOperator<T> operator) {
        this.map(operator)
    }

    default void rightShift(Function<T, Provider<T>> function) {
        this.flatMap(function)
    }

}