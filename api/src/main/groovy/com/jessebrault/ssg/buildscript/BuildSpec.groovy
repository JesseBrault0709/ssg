package com.jessebrault.ssg.buildscript

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.PackageScope
import groovy.transform.TupleConstructor

@PackageScope
@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
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

    @Override
    String toString() {
        "BuildSpec(name: ${ this.name }, isAbstract: ${ this.isAbstract }, extending: ${ this.extending })"
    }

}
