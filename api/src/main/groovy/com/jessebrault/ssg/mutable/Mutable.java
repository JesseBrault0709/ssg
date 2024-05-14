package com.jessebrault.ssg.mutable;

import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.function.*;

@ApiStatus.Experimental
@Deprecated
public interface Mutable<T> {
    T get();
    void set(T t);
    void unset();

    boolean isPresent();

    default boolean isEmpty() {
        return !this.isPresent();
    }

    void filterInPlace(Predicate<T> filter);
    void mapInPlace(UnaryOperator<T> mapper);
    <U> void zipInPlace(Mutable<U> other, Supplier<T> onEmpty, Supplier<U> onOtherEmpty, BiFunction<T, U, T> zipper);

    <U> U match(Supplier<U> onEmpty, Function<T, U> onPresentMapper);
    T getOrElse(Supplier<T> onEmpty);

    <U> Mutable<U> chain(Function<T, Mutable<U>> mapper);
    <U> Mutable<U> map(Function<T, U> mapper);

    <U, R> Mutable<R> zip(Mutable<U> other, BiFunction<T, U, R> zipper);

    Optional<T> asOptional();
}
