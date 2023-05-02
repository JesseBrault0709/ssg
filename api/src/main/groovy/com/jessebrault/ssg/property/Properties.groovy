package com.jessebrault.ssg.property

final class Properties {

    static <T> Property<T> get() {
        new SimpleProperty<>()
    }

    private Properties() {}

}
