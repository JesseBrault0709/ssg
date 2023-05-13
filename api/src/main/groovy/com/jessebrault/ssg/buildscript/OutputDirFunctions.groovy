package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.util.Monoid
import com.jessebrault.ssg.util.Monoids

import java.util.function.Function

final class OutputDirFunctions {

    static final Function<Build, OutputDir> DEFAULT = { Build build -> new OutputDir(build.name) }

    static final Monoid<Function<Build, OutputDir>> DEFAULT_MONOID = Monoids.of(DEFAULT, OutputDirFunctions::concat)

    static Function<Build, OutputDir> concat(
            Function<Build, OutputDir> f0,
            Function<Build, OutputDir> f1
    ) {
        f0 == OutputDirFunctions.DEFAULT ? f1 : f0
    }

    static Function<Build, OutputDir> of(File dir) {
        return { new OutputDir(dir) }
    }

    static Function<Build, OutputDir> of(String path) {
        return { new OutputDir(path) }
    }

    private OutputDirFunctions() {}

}
