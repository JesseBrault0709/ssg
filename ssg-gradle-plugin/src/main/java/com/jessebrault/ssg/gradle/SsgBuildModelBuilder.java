package com.jessebrault.ssg.gradle;

import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskCollection;
import org.gradle.jvm.tasks.Jar;
import org.gradle.tooling.provider.model.ToolingModelBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class SsgBuildModelBuilder implements ToolingModelBuilder {

    @Override
    public boolean canBuild(@NotNull String modelName) {
        return modelName.equals(SsgBuildModel.class.getName());
    }

    @Override
    public @NotNull Object buildAll(@NotNull String modelName, @NotNull Project project) {
        final SsgBuildModel ssgBuildModel = new DefaultSsgBuildModel();

        final JavaPluginExtension javaPluginExtension = project.getExtensions().getByType(JavaPluginExtension.class);
        final SourceSet mainSourceSet = javaPluginExtension.getSourceSets().getByName("main");
        final Jar jarTask = project.getTasks()
                .named(mainSourceSet.getJarTaskName(), Jar.class)
                .get();
        ssgBuildModel.getBuildOutputLibs().addAll(jarTask.getOutputs().getFiles().getFiles());

        final TaskCollection<Jar> allJarTasks = project.getTasks().withType(Jar.class);
        ssgBuildModel.getBuildOutputLibs().addAll(
                allJarTasks.stream()
                        .flatMap(jar -> jar.getOutputs().getFiles().getFiles().stream())
                        .collect(Collectors.toSet())
        );

        ssgBuildModel.getRuntimeClasspath().addAll(mainSourceSet.getRuntimeClasspath().getFiles());

        return ssgBuildModel;
    }

}
