package com.jessebrault.ssg.buildscript

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

import java.util.function.Function

import static org.junit.jupiter.api.Assertions.assertEquals

@ExtendWith(MockitoExtension)
final class BuildSpecUtilTests {

    @Test
    void overwrittenOutputDir(@Mock Function<Build, OutputDir> spec1OutputDirFunction) {
        def spec0 = new BuildSpec('spec0', true, BuildExtension.getEmpty(), {
            outputDirFunction = { }
        })
        def spec1 = new BuildSpec('spec1', false, BuildExtension.get('spec0'), {
            outputDirFunction = spec1OutputDirFunction
        })
        def r = BuildSpecUtil.getBuilds([spec0, spec1])
        assertEquals(1, r.size())
        def b0 = r[0]
        assertEquals('spec1', b0.name)
        assertEquals(spec1OutputDirFunction, b0.outputDirFunction)
    }

    @Test
    void outputDirManualConcat() {
        def spec0 = new BuildSpec('spec0', true, BuildExtension.getEmpty(), {
            outputDirFunction = OutputDirFunctions.DEFAULT
        })
        def spec1 = new BuildSpec('spec1', false, BuildExtension.get('spec0'), {
            outputDirFunction {
                it.andThen {
                    new OutputDir(new File(it.asFile(), 'spec1'))
                }
            }
        })
        def r = BuildSpecUtil.getBuilds([spec0, spec1])
        assertEquals(1, r.size())
        def b0 = r[0]
        assertEquals('spec1', b0.name)
        assertEquals('spec1/spec1', b0.outputDirFunction.apply(b0).asString())
    }

}
