package com.jessebrault.ssg.buildscript.delegates

import com.jessebrault.ssg.task.TaskFactory
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.function.Executable
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

import java.util.function.Consumer
import java.util.function.Supplier

import static org.junit.jupiter.api.Assertions.*

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
        def testSpec = result.find { it.name == 'test' }
        assertNotNull(testSpec)
        assertEquals(taskFactorySupplier, testSpec.supplier)
        assertTrue(testSpec.configurators.contains(taskFactoryConsumer))
    }

    @Test
    void registerAndConfigure(
            @Mock Supplier<TaskFactory> taskFactorySupplier,
            @Mock Consumer<TaskFactory> taskFactoryConsumer
    ) {
        def d = new TaskFactoriesDelegate()
        d.register('test', taskFactorySupplier, taskFactoryConsumer)
        def result = d.getResult()
        def testSpec = result.find { it.name == 'test' }
        assertNotNull(testSpec)
        assertEquals(taskFactorySupplier, testSpec.supplier)
        assertTrue(testSpec.configurators.contains(taskFactoryConsumer))
    }

}
