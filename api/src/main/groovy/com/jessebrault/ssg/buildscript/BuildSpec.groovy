package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.buildscript.delegates.BuildDelegate
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck

@NullCheck
@EqualsAndHashCode(excludes = 'buildClosure')
final class BuildSpec {

    static BuildSpec getEmpty() {
        new BuildSpec('', false, [], { })
    }

    static BuildSpec get(Map<String, Object> args) {
        new BuildSpec(
                args.name as String ?: '',
                args.isAbstract as boolean ?: false,
                args.extending as Collection<String> ?: [],
                args.buildClosure as Closure<?> ?: { }
        )
    }

    static BuildSpec get(
            String name,
            boolean isAbstract,
            Collection<String> extending,
            @DelegatesTo(value = BuildDelegate, strategy = Closure.DELEGATE_FIRST)
            Closure<?> buildClosure
    ) {
        new BuildSpec(name, isAbstract, extending, buildClosure)
    }

    final String name
    final boolean isAbstract
    final Collection<String> extending
    final Closure<?> buildClosure

    private BuildSpec(
            String name,
            boolean isAbstract,
            Collection<String> extending,
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
