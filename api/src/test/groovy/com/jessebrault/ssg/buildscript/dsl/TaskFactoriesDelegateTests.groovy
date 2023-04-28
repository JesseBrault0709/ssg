package com.jessebrault.ssg.buildscript.dsl

import com.jessebrault.ssg.task.TaskFactory
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.function.Executable
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

import java.util.function.Consumer
import java.util.function.Supplier

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

@ExtendWith(MockitoExtension)
final class TaskFactoriesDelegateTests {

    @Test
    void registerThenConfigure(
            @Mock Supplier<TaskFactory> taskFactorySupplier,
            @Mock Consumer<TaskFactory> taskFactoryConsumer
    ) {
        def d = new TaskFactoriesDelegate()
        d.register('test', taskFactorySupplier)
        assertDoesNotThrow({
            d.configure('test', TaskFactory, taskFactoryConsumer)
        } as Executable)
        def result = d.getResult()
        assertTrue(result.containsKey('test'))
        assertEquals(taskFactorySupplier, result['test'].supplier)
        assertTrue(result['test'].configurators.contains(taskFactoryConsumer))
    }

    @Test
    void registerAndConfigure(
            @Mock Supplier<TaskFactory> taskFactorySupplier,
            @Mock Consumer<TaskFactory> taskFactoryConsumer
    ) {
        def d = new TaskFactoriesDelegate()
        d.register('test', taskFactorySupplier, taskFactoryConsumer)
        def result = d.getResult()
        assertTrue(result.containsKey('test'))
        assertEquals(taskFactorySupplier, result['test'].supplier)
        assertTrue(result['test'].configurators.contains(taskFactoryConsumer))
    }

}
