package com.jessebrault.ssg.buildscript.dsl

import com.jessebrault.ssg.buildscript.Build
import com.jessebrault.ssg.buildscript.OutputDir
import com.jessebrault.ssg.buildscript.OutputDirFunctions
import org.jetbrains.annotations.Nullable

import java.util.function.Function

final class BuildDelegate extends AbstractBuildDelegate<Build> {

    String name = ''
    private Function<Build, OutputDir> outputDirFunction = OutputDirFunctions.DEFAULT

    @Override
    Build getResult() {
        new Build(
                this.name,
                this.outputDirFunction,
                this.getSiteSpecResult(),
                this.getGlobalsResult(),
                this.getTaskFactoriesResult(this.getSourcesResult(this.getTypesResult()))
        )
    }

    void setOutputDirFunction(Function<Build, OutputDir> outputDirFunction) {
        this.outputDirFunction = outputDirFunction
    }

    void setOutputDir(File file) {
        this.outputDirFunction = OutputDirFunctions.of(file)
    }

    void setOutputDir(@Nullable String path) {
        this.outputDirFunction = OutputDirFunctions.of(path)
    }

}
