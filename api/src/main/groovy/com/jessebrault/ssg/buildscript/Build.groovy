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
final class Build {

    static Build getEmpty() {
        new Build(
                '',
                OutputDirFunctions.DEFAULT,
                SiteSpec.getBlank(),
                [:],
                [],
                []
        )
    }

    static Build get(Map<String, Object> args) {
        new Build(
                args.name as String ?: '',
                args.outputDirFunction as Function<Build, OutputDir> ?: OutputDirFunctions.DEFAULT,
                args.siteSpec as SiteSpec ?: SiteSpec.getBlank(),
                args.globals as Map<String, Object> ?: [:],
                args.taskFactorySpecs as Collection<TaskFactorySpec<TaskFactory>> ?: [],
                args.includedBuilds as Collection<String> ?: []
        )
    }

    static Build concat(Build b0, Build b1) {
        new Build(
                b0.name.blank ? b1.name : b0.name,
                OutputDirFunctions.concat(b0.outputDirFunction, b1.outputDirFunction),
                SiteSpec.concat(b0.siteSpec, b1.siteSpec),
                b0.globals + b1.globals,
                b0.taskFactorySpecs + b1.taskFactorySpecs,
                b0.includedBuilds + b1.includedBuilds
        )
    }

    final String name
    final Function<Build, OutputDir> outputDirFunction
    final SiteSpec siteSpec
    final Map<String, Object> globals
    final Collection<TaskFactorySpec<TaskFactory>> taskFactorySpecs
    final Collection<String> includedBuilds

    Build plus(Build other) {
        concat(this, other)
    }

    @Override
    String toString() {
        "Build(name: ${ this.name }, siteSpec: ${ this.siteSpec }, globals: ${ this.globals }, includedBuilds: ${ this.includedBuilds })"
    }

}
