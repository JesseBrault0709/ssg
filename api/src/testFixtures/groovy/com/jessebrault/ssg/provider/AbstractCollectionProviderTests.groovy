package com.jessebrault.ssg.provider

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.function.Consumer

import static org.junit.jupiter.api.Assertions.assertTrue

abstract class AbstractCollectionProviderTests {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractCollectionProviderTests)

    protected abstract CollectionProvider<Integer> getCollectionProvider(Collection<Integer> ts)

    @Test
    void providesTs() {
        def ts = [0, 1]
        def result = this.getCollectionProvider(ts).provide()
        logger.debug('result: {}', result)
        assertTrue(result.size() == 2)
        assertTrue(result.inject(true) { acc, val ->
            acc && ts.contains(val)
        })
    }

    @Test
    void collectionsAdded() {
        def ts0 = [0, 1]
        def ts1 = [2, 3]
        def allTs = ts0 + ts1
        def p0 = this.getCollectionProvider(ts0)
        def p1 = this.getCollectionProvider(ts1)
        def sum = p0 + p1
        def result = sum.provide()
        logger.debug('result: {}', result)
        assertTrue(result.size() == 4)
        assertTrue(result.inject(true) { acc, val ->
            acc && allTs.contains(val)
        })
    }

    @SuppressWarnings('GrMethodMayBeStatic')
    @TestFactory
    Collection<DynamicTest> containsAndIsCaseCollectionProviderChildren() {
        def p0 = this.getCollectionProvider([])
        def p1 = this.getCollectionProvider([])
        def sum = p0 + p1
        ([
                contains: { CollectionProvider<Integer> p ->
                    assertTrue(p.contains(p0))
                    assertTrue(p.contains(p1))
                } as Consumer<CollectionProvider<Integer>>,
                in: { CollectionProvider<Integer> p ->
                    assertTrue(p0 in p)
                    assertTrue(p1 in p)
                } as Consumer<CollectionProvider<Integer>>
        ]).collect { entry ->
            DynamicTest.dynamicTest(entry.key, { entry.value.accept(sum) })
        }
    }

    @SuppressWarnings('GrMethodMayBeStatic')
    @TestFactory
    Collection<DynamicTest> containsAndIsCaseProviderChildren() {
        def p0 = this.getCollectionProvider([])
        def p1 = this.getCollectionProvider([])
        def sum = p0 + p1
        ([
                contains: { CollectionProvider<Integer> p ->
                    assertTrue(p.contains(p0))
                    assertTrue(p.contains(p1))
                } as Consumer<CollectionProvider<Integer>>,
                in: { CollectionProvider<Integer> p ->
                    assertTrue(p0 in p)
                    assertTrue(p1 in p)
                } as Consumer<CollectionProvider<Integer>>
        ]).collect { entry ->
            DynamicTest.dynamicTest(entry.key, { entry.value.accept(sum) })
        }
    }

}
