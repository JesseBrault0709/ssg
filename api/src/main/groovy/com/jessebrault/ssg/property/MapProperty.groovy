package com.jessebrault.ssg.property

interface MapProperty<K, V> extends Property<Map<K, V>> {
    V get(K key)
    void put(K key, V value)
    void putAll(Map<? extends K, ? extends V> map)
}
