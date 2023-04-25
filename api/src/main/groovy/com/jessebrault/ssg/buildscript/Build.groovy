package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.SiteSpec
import com.jessebrault.ssg.task.TaskFactorySpec
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

import java.util.function.Function

@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class Build {

    @TupleConstructor(defaults = false)
    @NullCheck(includeGenerated = true)
    @EqualsAndHashCode
    static final class AllBuilds {

        static AllBuilds concat(AllBuilds ab0, AllBuilds ab1) {
            new AllBuilds(
                    ab0.siteSpec + ab1.siteSpec,
                    ab0.globals + ab1.globals,
                    ab0.taskFactorySpecs + ab1.taskFactorySpecs
            )
        }

        static AllBuilds getEmpty() {
            new AllBuilds(SiteSpec.getBlank(), [:], [])
        }

        final SiteSpec siteSpec
        final Map<String, Object> globals
        final Collection<TaskFactorySpec> taskFactorySpecs

        AllBuilds plus(AllBuilds other) {
            concat(this, other)
        }

    }

    static Build getEmpty() {
        new Build(
                '',
                OutputDirFunctions.DEFAULT,
                SiteSpec.getBlank(),
                [:],
                []
        )
    }

    static Build get(Map<String, Object> args) {
        new Build(
                args.name as String ?: '',
                args.outputDirFunction as Function<Build, OutputDir> ?: OutputDirFunctions.DEFAULT,
                args.siteSpec as SiteSpec ?: SiteSpec.getBlank(),
                args.globals as Map<String, Object> ?: [:],
                args.taskFactorySpecs as Collection<TaskFactorySpec> ?: []
        )
    }

    static Build concat(Build b0, Build b1) {
        new Build(
                b1.name.blank ? b0.name : b1.name,
                OutputDirFunctions.concat(b0.outputDirFunction, b1.outputDirFunction),
                SiteSpec.concat(b0.siteSpec, b1.siteSpec),
                b0.globals + b1.globals,
                b0.taskFactorySpecs + b1.taskFactorySpecs
        )
    }

    static Build from(AllBuilds allBuilds) {
        new Build(
                '',
                OutputDirFunctions.DEFAULT,
                allBuilds.siteSpec,
                allBuilds.globals,
                allBuilds.taskFactorySpecs
        )
    }

    final String name
    final Function<Build, OutputDir> outputDirFunction
    final SiteSpec siteSpec
    final Map<String, Object> globals
    final Collection<TaskFactorySpec> taskFactorySpecs

    Build plus(Build other) {
        concat(this, other)
    }

}
