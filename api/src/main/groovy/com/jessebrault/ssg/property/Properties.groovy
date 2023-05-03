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

    static <K, V> MapProperty<K, V> getMap() {
        new SimpleMapProperty<K, V>()
    }

    static <K, V> MapProperty<K, V> getMap(Map<? extends K, ? extends V> convention) {
        new SimpleMapProperty<K, V>(convention)
    }

    static <K, V> MapProperty<K, V> getMap(
            Map<? extends K, ? extends V> convention,
            Map<? extends K, ? extends V> value
    ) {
        new SimpleMapProperty<K, V>(convention, value)
    }

    private Properties() {}

}
