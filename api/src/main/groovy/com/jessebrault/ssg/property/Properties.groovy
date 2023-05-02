package com.jessebrault.ssg.property

final class Properties {

    static <T> Property<T> get() {
        new SimpleProperty<>()
    }

    static <T> Property<T> get(T convention) {
        new SimpleProperty<>(convention)
    }

    static <T> Property<T> get(T convention, T t) {
        new SimpleProperty<>(convention, t)
    }

    private Properties() {}

}
