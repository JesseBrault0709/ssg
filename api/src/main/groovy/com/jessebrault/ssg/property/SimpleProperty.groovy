package com.jessebrault.ssg.property

import com.jessebrault.ssg.provider.AbstractProvider
import com.jessebrault.ssg.provider.Provider
import com.jessebrault.ssg.provider.Providers
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.PackageScope

import java.util.function.Function
import java.util.function.UnaryOperator

@PackageScope
@NullCheck
@EqualsAndHashCode(includeFields = true)
final class SimpleProperty<T> extends AbstractProvider<T> implements Property<T> {

    private Provider<T> convention = Providers.getEmpty()
    private Provider<T> tProvider = Providers.getEmpty()

    @Override
    T provide() {
        this.tProvider.present ? this.tProvider.provide() : this.convention.provide()
    }

    @Override
    void set(T t) {
        this.tProvider = Providers.of(t)
    }

    @Override
    void set(Provider<T> provider) {
        this.tProvider = provider
    }

    @Override
    void unset() {
        this.tProvider = Providers.getEmpty()
    }

    @Override
    T getConvention() {
        this.convention.provide()
    }

    @Override
    void setConvention(T t) {
        this.convention = Providers.of(t)
    }

    @Override
    void setConvention(Provider<T> provider) {
        this.convention = provider
    }

    @Override
    void unsetConvention() {
        this.convention = Providers.getEmpty()
    }

    @Override
    void map(UnaryOperator<T> operator) {
        def oldTProvider = this.tProvider
        this.tProvider = Providers.fromSupplier {
            operator.apply(oldTProvider.provide())
        }
    }

    @Override
    void flatMap(Function<T, Provider<T>> function) {
        def oldTProvider = this.tProvider
        this.tProvider = Providers.fromSupplier {
            function.apply(oldTProvider.provide()).provide()
        }
    }

    @Override
    String toString() {
        "SimpleProperty(convention: ${ this.convention }, tProvider: ${ this.tProvider })"
    }

}
