package com.jessebrault.ssg.mutable;

import com.jessebrault.ssg.util.Monoid;
import com.jessebrault.ssg.util.Monoids;
import com.jessebrault.ssg.util.Semigroup;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Experimental
public final class Mutables {

    public static <T> Monoid<Mutable<T>> getMonoid(final Semigroup<T> tSemigroup) {
        return Monoids.of(Mutables.getEmpty(), (m0, m1) -> {
            if (m0.isPresent() && m1.isPresent()) {
                return get(tSemigroup.getConcat().apply(m0.get(), m1.get()));
            } else if (m0.isPresent()) {
                return m0;
            } else if (m1.isPresent()) {
                return m1;
            } else {
                return getEmpty();
            }
        });
    }

    public static <T> @NotNull Mutable<T> getEmpty() {
        return new SimpleMutable<>();
    }

    public static <T> Mutable<T> get(T initialValue) {
        return new SimpleMutable<>(initialValue);
    }

    private Mutables() {}

}
