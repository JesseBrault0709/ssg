package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.SiteSpec
import com.jessebrault.ssg.task.TaskFactory
import com.jessebrault.ssg.task.TaskFactorySpec
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

import java.util.function.Function
import java.util.function.Supplier

import static org.junit.jupiter.api.Assertions.assertEquals

@ExtendWith(MockitoExtension)
final class BuildTests {

    @Test
    void twoEmptiesEqual() {
        def b0 = Build.getEmpty()
        def b1 = Build.getEmpty()
        assertEquals(b0, b1)
    }

    @Test
    void ifName0BlankTakeName1() {
        def b0 = Build.get(name: '')
        def b1 = Build.get(name: 'test')
        def sum = b0 + b1
        assertEquals('test', sum.name)
    }

    @Test
    void ifName0NotBlankTakeName0() {
        def b0 = Build.get(name: 'b0')
        def b1 = Build.get(name: 'b1')
        def sum = b0 + b1
        assertEquals('b0', sum.name)
    }

    @Test
    void ifOutputDirFunction0DefaultTake1(@Mock Function<Build, OutputDir> b1OutputDirFunction) {
        def b0 = Build.get(outputDirFunction: OutputDirFunctions.DEFAULT)
        def b1 = Build.get(outputDirFunction: b1OutputDirFunction)
        def sum = b0 + b1
        assertEquals(b1OutputDirFunction, sum.outputDirFunction)
    }

    @Test
    void siteSpecsAdded() {
        def b0 = Build.get(
                siteSpec: new SiteSpec('test', '')
        )
        def b1 = Build.get(
                siteSpec: new SiteSpec('', 'test')
        )
        def sum = b0 + b1
        assertEquals(new SiteSpec('test', 'test'), sum.siteSpec)
    }

    @Test
    void globalsAdded() {
        def b0 = Build.get(globals: [a: 0])
        def b1 = Build.get(globals: [b: 1])
        def sum = b0 + b1
        assertEquals([a: 0, b: 1], sum.globals)
    }

    @Test
    void taskFactorySpecsAdded(@Mock Supplier<TaskFactory> taskFactorySupplier) {
        def spec0 = new TaskFactorySpec<>('spec0', taskFactorySupplier, [])
        def spec1 = new TaskFactorySpec<>('spec1', taskFactorySupplier, [])
        def b0 = Build.get(taskFactorySpecs: [spec0])
        def b1 = Build.get(taskFactorySpecs: [spec1])
        def sum = b0 + b1
        assertEquals([spec0, spec1], sum.taskFactorySpecs)
    }

}
