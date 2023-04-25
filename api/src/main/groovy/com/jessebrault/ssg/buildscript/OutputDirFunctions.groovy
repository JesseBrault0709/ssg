package com.jessebrault.ssg.buildscript

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType

import java.util.function.Function

final class OutputDirFunctions {

    static final Function<Build, OutputDir> DEFAULT = of { new OutputDir(it.name) }

    static Function<Build, OutputDir> concat(
            Function<Build, OutputDir> f0,
            Function<Build, OutputDir> f1
    ) {
        f1 == OutputDirFunctions.DEFAULT ? f0 : f1
    }

    static Function<Build, OutputDir> of(
            @ClosureParams(value = SimpleType, options = 'com.jessebrault.ssg.buildscript.Build')
            Closure<OutputDir> closure
    ) {
        closure as Function<Build, OutputDir>
    }

    static Function<Build, OutputDir> of(File dir) {
        of { new OutputDir(dir) }
    }

    static Function<Build, OutputDir> of(String path) {
        of { new OutputDir(path) }
    }

    private OutputDirFunctions() {}

}
