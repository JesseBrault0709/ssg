package com.jessebrault.ssg.buildscript

import groovy.transform.EqualsAndHashCode
import org.jetbrains.annotations.Nullable

@EqualsAndHashCode
final class OutputDir {

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

}
