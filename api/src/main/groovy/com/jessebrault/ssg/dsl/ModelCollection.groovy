package com.jessebrault.ssg.dsl

import com.jessebrault.ssg.model.Model
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import org.jetbrains.annotations.Nullable

@NullCheck
@EqualsAndHashCode(includeFields = true)
final class ModelCollection<T> {

    @Delegate
    private final Collection<Model<T>> ts = []

    ModelCollection(Collection<Model<T>> ts) {
        this.ts.addAll(ts)
    }

    @Nullable
    Model<T> getByName(String name) {
        this.ts.find { it.name == name }
    }

    @Nullable
    <E extends T> Model<E> getByNameAndType(String name, Class<E> type) {
        this.ts.find { it.name == name && type.isAssignableFrom(it.get().class) } as Model<E>
    }

    Optional<Model<T>> findByName(String name) {
        Optional.ofNullable(this.getByName(name))
    }

    def <E extends T> Optional<Model<E>> findByNameAndType(String name, Class<E> type) {
        Optional.ofNullable(this.getByNameAndType(name, type))
    }

    def <E extends T> ModelCollection<E> findAllByType(Class<E> type) {
        def es = this.ts.findResults {
            type.isAssignableFrom(it.get().class) ? it as Model<E> : null
        }
        new ModelCollection<>(es)
    }

}
