package com.jessebrault.ssg.dsl

import com.jessebrault.ssg.model.Model
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import org.jetbrains.annotations.Nullable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.MarkerFactory

import java.util.function.Predicate

@NullCheck
@EqualsAndHashCode(includeFields = true)
final class ModelCollection<T> {

    private static final Logger logger = LoggerFactory.getLogger(ModelCollection)
    private static final Marker enter = MarkerFactory.getMarker('ENTER')
    private static final Marker exit = MarkerFactory.getMarker('EXIT')

    @Delegate
    private final Collection<Model<T>> models = []

    ModelCollection(Collection<Model<T>> models) {
        this.models.addAll(models)
    }

    @Nullable
    Model<T> getByName(String name) {
        this.models.find { it.name == name }
    }

    @Nullable
    <E extends T> Model<E> getByNameAndType(String name, Class<E> type) {
        this.models.find { it.name == name && type.isAssignableFrom(it.get().class) } as Model<E>
    }

    Optional<Model<T>> findByName(String name) {
        Optional.ofNullable(this.getByName(name))
    }

    def <E extends T> Optional<Model<E>> findByNameAndType(String name, Class<E> type) {
        Optional.ofNullable(this.getByNameAndType(name, type))
    }

    def <E> ModelCollection<E> findAllByType(Class<E> type) {
        logger.trace(enter, 'type: {}', type)
        def es = this.models.findResults {
            def itType = it.get().class
            def itResult = type.isAssignableFrom(itType)
            logger.debug(
                    'it: {}, itType: {}, itType.classLoader: {}, itResult: {}',
                    it, itType, itType.classLoader, itResult
            )
            itResult ? it as Model<E> : null
        }
        def result = new ModelCollection<>(es)
        logger.trace(exit, 'result: {}', result)
        result
    }

    def <E extends T> Optional<Model<E>> findOne(Class<E> type, Predicate<E> filter) {
        Optional.ofNullable(this.models.find {
            def t = it.get()
            if (type.isAssignableFrom(t.class)) {
                filter.test(t as E) ? it : null
            } else {
                null
            }
        } as Model<E>)
    }

    def <E extends T> Optional<Model<E>> findOne(Class<E> type) {
        this.findOne(type) { true }
    }

    Optional<Model<T>> findOne(Predicate<T> filter) {
        Optional.ofNullable(this.models.find {
            filter.test(it.get()) ? it : null
        })
    }

}
