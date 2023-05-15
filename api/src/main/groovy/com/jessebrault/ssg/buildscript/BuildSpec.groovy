package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.buildscript.delegates.BuildDelegate
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.PackageScope
import groovy.transform.TupleConstructor

@PackageScope
@NullCheck()
@EqualsAndHashCode(excludes = 'buildClosure')
final class BuildSpec {

    static BuildSpec getEmpty() {
        new BuildSpec('', false, BuildExtension.getEmpty(), { })
    }

    static BuildSpec get(Map<String, Object> args) {
        new BuildSpec(
                args.name as String ?: '',
                args.isAbstract as boolean ?: false,
                args.extending as BuildExtension ?: BuildExtension.getEmpty(),
                args.buildClosure as Closure<?> ?: { }
        )
    }

    final String name
    final boolean isAbstract
    final BuildExtension extending
    final Closure<?> buildClosure

    BuildSpec(
            String name,
            boolean isAbstract,
            BuildExtension extending,
            @DelegatesTo(value = BuildDelegate, strategy = Closure.DELEGATE_FIRST)
            Closure<?> buildClosure
    ) {
        this.name = name
        this.isAbstract = isAbstract
        this.extending = extending
        this.buildClosure = buildClosure
    }

    @Override
    String toString() {
        "BuildSpec(name: ${ this.name }, isAbstract: ${ this.isAbstract }, extending: ${ this.extending })"
    }

}
