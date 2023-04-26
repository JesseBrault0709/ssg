package com.jessebrault.ssg.buildscript

import org.jetbrains.annotations.Nullable

import java.util.function.Function

final class OutputDirFunctions {

    static final Function<Build, OutputDir> DEFAULT = { Build build -> new OutputDir(build.name) }

    static Function<Build, OutputDir> concat(
            Function<Build, OutputDir> f0,
            Function<Build, OutputDir> f1
    ) {
        f1 == OutputDirFunctions.DEFAULT ? f0 : f1
    }

    static Function<Build, OutputDir> of(File dir) {
        return { new OutputDir(dir) }
    }

    static Function<Build, OutputDir> of(@Nullable String path) {
        return { new OutputDir(path) }
    }

    private OutputDirFunctions() {}

}
