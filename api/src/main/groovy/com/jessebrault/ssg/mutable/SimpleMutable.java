package com.jessebrault.ssg.mutable;

import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.function.*;

@ApiStatus.Experimental
final class SimpleMutable<T> implements Mutable<T> {

    private T t;

    public SimpleMutable(T initialValue) {
        this.t = initialValue;
    }

    public SimpleMutable() {}

    @Override
    public T get() {
        if (this.t == null) {
            throw new NullPointerException();
        }
        return this.t;
    }

    @Override
    public void set(T t) {
        this.t = t;
    }

    @Override
    public void unset() {
        this.t = null;
    }

    @Override
    public boolean isPresent() {
        return this.t != null;
    }

    @Override
    public void filterInPlace(Predicate<T> filter) {
        if (this.t != null && !filter.test(this.t)) {
            this.unset();
        }
    }

    @Override
    public void mapInPlace(UnaryOperator<T> mapper) {
        this.t = mapper.apply(this.t);
    }

    @Override
    public <U> void zipInPlace(Mutable<U> other, Supplier<T> onEmpty, Supplier<U> onOtherEmpty, BiFunction<T, U, T> zipper) {
        this.t = zipper.apply(
                this.isPresent() ? this.t : onEmpty.get(),
                other.isPresent() ? other.get() : onOtherEmpty.get()
        );
    }

    @Override
    public <U> U match(Supplier<U> onEmpty, Function<T, U> onPresentMapper) {
        return this.t != null ? onPresentMapper.apply(this.t) : onEmpty.get();
    }

    @Override
    public T getOrElse(Supplier<T> other) {
        return this.t != null ? this.t : other.get();
    }

    @Override
    public <U> Mutable<U> chain(Function<T, Mutable<U>> mapper) {
        return this.map(mapper).get();
    }

    @Override
    public <U> Mutable<U> map(Function<T, U> mapper) {
        return this.t != null ? Mutables.get(mapper.apply(this.t)) : Mutables.getEmpty();
    }

    @Override
    public <U, R> Mutable<R> zip(Mutable<U> other, BiFunction<T, U, R> zipper) {
        return this.isPresent() && other.isPresent()
                ? Mutables.get(zipper.apply(this.get(), other.get()))
                : Mutables.getEmpty();
    }

    @Override
    public Optional<T> asOptional() {
        return Optional.ofNullable(this.t);
    }

}
