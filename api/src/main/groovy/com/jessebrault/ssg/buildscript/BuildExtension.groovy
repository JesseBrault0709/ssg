package com.jessebrault.ssg.buildscript

import groovy.transform.NullCheck
import groovy.transform.PackageScope

@PackageScope
@NullCheck
final class BuildExtension {

    static BuildExtension getEmpty() {
        new BuildExtension()
    }

    static BuildExtension get(String buildName) {
        new BuildExtension(buildName)
    }

    private final String buildName

    private BuildExtension(String buildName) {
        this.buildName = buildName
    }

    private BuildExtension() {
        this.buildName = null
    }

    boolean isPresent() {
        this.buildName != null
    }

    boolean isEmpty() {
        !this.present
    }

    String getBuildName() {
        Objects.requireNonNull(this.buildName)
    }

    @Override
    String toString() {
        this.present ? "BuildExtension(extending: ${ this.buildName })" : "BuildExtension(empty)"
    }

    @Override
    int hashCode() {
        Objects.hash(this.buildName)
    }

    @Override
    boolean equals(Object obj) {
        obj.is(this)
                || (obj instanceof BuildExtension && obj.present && obj.buildName == this.buildName)
                || (obj instanceof BuildExtension && !obj.present && !this.present)
    }

}
