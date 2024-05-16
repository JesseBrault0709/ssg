package com.jessebrault.ssg.gradle;

import java.io.File;
import java.util.Set;

public interface SsgBuildModel {
    Set<File> getBuildOutputLibs();
    Set<File> getRuntimeClasspath();
}
