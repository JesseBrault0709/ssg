package com.jessebrault.ssg.property

import com.jessebrault.ssg.util.Monoid

final class Properties {

    static <T> Property<T> get(Monoid<T> semiGroup) {
        new SimpleProperty<>(semiGroup)
    }

    static <T> Property<T> get(Monoid<T> semiGroup, T convention) {
        new SimpleProperty<>(semiGroup, convention)
    }

    static <T> Property<T> get(Monoid<T> semiGroup, T convention, T t) {
        new SimpleProperty<>(semiGroup, convention, t)
    }

    private Properties() {}

}
