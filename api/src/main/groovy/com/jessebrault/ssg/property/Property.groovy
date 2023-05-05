package com.jessebrault.ssg.property

import java.util.function.UnaryOperator

interface Property<T> {
    T get()
    void set(T t)
    void unset()

    T getConvention()
    void setConvention(T t)
    void unsetConvention()

    void map(UnaryOperator<T> mapper)
    void merge(
            @DelegatesTo(type = 'T', strategy = Closure.DELEGATE_FIRST)
            Closure<?> configurator
    )
}