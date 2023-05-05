package com.jessebrault.ssg.property

import com.jessebrault.ssg.util.Monoid
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.PackageScope

import java.util.function.UnaryOperator

import static java.util.Objects.requireNonNull

@PackageScope
@NullCheck(includeGenerated = true)
@EqualsAndHashCode(includeFields = true)
final class SimpleProperty<T> implements Property<T> {

    private final Monoid<T> monoid

    private T t
    private T convention

    SimpleProperty(Monoid<T> monoid) {
        this.monoid = monoid
        this.convention = this.monoid.empty.get()
    }

    SimpleProperty(Monoid<T> monoid, T convention) {
        this.monoid = monoid
        this.convention = convention
    }

    SimpleProperty(Monoid<T> monoid, T convention, T t) {
        this.monoid = monoid
        this.t = t
        this.convention = convention
    }

    @Override
    T get() {
        this.t != null ? this.t : requireNonNull(this.convention)
    }

    @Override
    void set(T t) {
        this.t = t
    }

    @Override
    void unset() {
        this.t = null
    }

    @Override
    T getConvention() {
        requireNonNull(this.convention)
    }

    @Override
    void setConvention(T t) {
        this.convention = t
    }

    @Override
    void unsetConvention() {
        this.t = null
    }

    @Override
    void map(UnaryOperator<T> mapper) {
        this.t = requireNonNull(mapper.apply(this.t))
    }

    @Override
    void merge(
            @DelegatesTo(type = 'T', strategy = Closure.DELEGATE_FIRST)
            Closure<?> configurator
    ) {
        def d = requireNonNull(this.monoid.empty.get())
        configurator.delegate = d
        configurator.resolveStrategy = Closure.DELEGATE_FIRST
        configurator()
        this.t = requireNonNull(this.monoid.concat.apply(this.t != null ? this.t : this.monoid.empty.get(), d))
    }

}
