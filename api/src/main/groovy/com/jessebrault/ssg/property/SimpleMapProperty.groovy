package com.jessebrault.ssg.property

import com.jessebrault.ssg.provider.CollectionProvider
import com.jessebrault.ssg.provider.Provider
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.PackageScope

import java.util.function.Function
import java.util.function.UnaryOperator

@PackageScope
@NullCheck
@EqualsAndHashCode(includeFields = true)
final class SimpleMapProperty<K, V> implements MapProperty<K, V> {

    private final Map<K, V> convention = [:]
    private final Map<K, V> m = [:]

    SimpleMapProperty(
            Map<? extends K, ? extends V> convention,
            Map<? extends K, ? extends V> m
    ) {
        this.convention.putAll(convention)
        this.m.putAll(m)
    }

    SimpleMapProperty(Map<? extends K, ? extends V> convention) {
        this(convention, [:] as Map<K, V>)
    }

    SimpleMapProperty() {}

    @Override
    V get(K key) {
        if (m[key] != null) {
            m[key]
        } else if (convention[key] != null) {
            convention[key]
        } else {
            throw new NullPointerException("no such key: ${ key }")
        }
    }

    @Override
    void put(K key, V value) {
        this.m.put(key, value)
    }

    @Override
    void putAll(Map<? extends K, ? extends V> map) {
        this.m.putAll(map)
    }

    @Override
    void set(Map<K, V> kvMap) {
        this.m.clear()
        this.m.putAll(kvMap)
    }

    @Override
    void set(Provider<Map<K, V>> provider) {
        // TODO
    }

    @Override
    void unset() {
        this.m.clear()
    }

    @Override
    Map<K, V> getConvention() {
        this.convention
    }

    @Override
    void setConvention(Map<K, V> kvMap) {
        this.convention.clear()
        this.convention.putAll(kvMap)
    }

    @Override
    void setConvention(Provider<Map<K, V>> provider) {
        // TODO
    }

    @Override
    void unsetConvention() {
        this.convention.clear()
    }

    @Override
    void map(UnaryOperator<Map<K, V>> operator) {
        this.m.putAll(operator.apply(this.convention + this.m))
    }

    @Override
    void flatMap(Function<Map<K, V>, Provider<Map<K, V>>> function) {
        // TODO
    }

    @Override
    Map<K, V> provide() {
        this.convention + this.m
    }

    @Override
    CollectionProvider<Map<K, V>> plus(Provider<Map<K, V>> other) {
        // TODO
    }

    @Override
    CollectionProvider<Map<K, V>> asType(Class<CollectionProvider> collectionProviderClass) {
        // TODO
    }

    @Override
    boolean isEmpty() {
        return false
    }

}
