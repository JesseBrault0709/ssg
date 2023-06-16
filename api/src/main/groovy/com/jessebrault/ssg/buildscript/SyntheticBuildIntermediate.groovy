package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.SiteSpec
import com.jessebrault.ssg.task.TaskFactory
import com.jessebrault.ssg.task.TaskFactorySpec
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

import java.util.function.Function

@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class SyntheticBuildIntermediate implements BuildIntermediate {

    static BuildIntermediate getEmpty() {
        new SyntheticBuildIntermediate(
                BuildSpec.getEmpty(),
                OutputDirFunctions.DEFAULT,
                SiteSpec.getBlank(),
                [:],
                TypesContainer.getEmpty(),
                SourceProviders.getEmpty(),
                [],
                []
        )
    }

    static BuildIntermediate get(Map<String, Object> args) {
        new SyntheticBuildIntermediate(
                args.buildSpec as BuildSpec ?: BuildSpec.getEmpty(),
                args.outputDirFunction as Function<Build, OutputDir> ?: OutputDirFunctions.DEFAULT,
                args.siteSpec as SiteSpec ?: SiteSpec.getBlank(),
                args.globals as Map<String, Object> ?: [:],
                args.types as TypesContainer ?: TypesContainer.getEmpty(),
                args.sources as SourceProviders ?: SourceProviders.getEmpty(),
                args.taskFactorySpecs as Collection<TaskFactorySpec<TaskFactory>> ?: [],
                args.includedBuilds as Collection<String> ?: []
        )
    }

    static BuildIntermediate get(
            BuildSpec buildSpec,
            Function<Build, OutputDir> outputDirFunction,
            SiteSpec siteSpec,
            Map<String, Object> globals,
            TypesContainer types,
            SourceProviders sources,
            Collection<TaskFactorySpec<TaskFactory>> taskFactorySpecs,
            Collection<String> includedBuilds
    ) {
        new SyntheticBuildIntermediate(
                buildSpec,
                outputDirFunction,
                siteSpec,
                globals,
                types,
                sources,
                taskFactorySpecs,
                includedBuilds
        )
    }

    final BuildSpec buildSpec
    final Function<Build, OutputDir> outputDirFunction
    final SiteSpec siteSpec
    final Map<String, Object> globals
    final TypesContainer types
    final SourceProviders sources
    final Collection<TaskFactorySpec<TaskFactory>> taskFactorySpecs
    final Collection<String> includedBuilds

}
