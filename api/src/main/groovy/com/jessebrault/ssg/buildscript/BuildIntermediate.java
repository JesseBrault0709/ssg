package com.jessebrault.ssg.buildscript;

import com.jessebrault.ssg.SiteSpec;
import com.jessebrault.ssg.task.TaskFactory;
import com.jessebrault.ssg.task.TaskFactorySpec;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public interface BuildIntermediate {
    BuildSpec getBuildSpec();
    Function<Build, OutputDir> getOutputDirFunction();
    SiteSpec getSiteSpec();
    Map<String, Object> getGlobals();
    TypesContainer getTypes();
    SourceProviders getSources();
    Collection<TaskFactorySpec<TaskFactory>> getTaskFactorySpecs();
    Collection<String> getIncludedBuilds();
}
