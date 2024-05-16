package com.jessebrault.ssg.gradle;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class DeafultSsgBuildModel implements SsgBuildModel, Serializable {

    private final Set<File> buildOutputLibs = new HashSet<>();
    private final Set<File> runtimeClasspath = new HashSet<>();

    @Override
    public Set<File> getBuildOutputLibs() {
        return this.buildOutputLibs;
    }

    @Override
    public Set<File> getRuntimeClasspath() {
        return this.runtimeClasspath;
    }

}
