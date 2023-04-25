package com.jessebrault.ssg.buildscript

import groovy.transform.EqualsAndHashCode
import org.jetbrains.annotations.Nullable

@EqualsAndHashCode
final class OutputDir {

    static OutputDir concat(OutputDir od0, OutputDir od1) {
        new OutputDir(od1.path ? od1.path : od0.path)
    }

    @Nullable
    final String path

    OutputDir(@Nullable String path) {
        this.path = path
    }

    OutputDir(File file) {
        this.path = file.path
    }

    File getFile() {
        this.path ? new File(this.path) : new File('')
    }

    OutputDir plus(OutputDir other) {
        concat(this, other)
    }

}
