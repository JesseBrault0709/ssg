package com.jessebrault.ssg.gradle;

import org.gradle.api.Project;
import org.gradle.jvm.tasks.Jar;
import org.gradle.tooling.provider.model.ToolingModelBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SsgBuildModelBuilder implements ToolingModelBuilder {

    @Override
    public boolean canBuild(@NotNull String modelName) {
        return modelName.equals(SsgBuildModel.class.getName());
    }

    @Override
    public @NotNull Object buildAll(@NotNull String modelName, @NotNull Project project) {
        final SsgBuildModel ssgBuildModel = new DefaultSsgBuildModel();

        final List<Jar> ssgJarTasks = project.getTasks().matching(task ->
                task instanceof Jar && task.getExtensions().findByType(SsgJarExtension.class) != null
        ).stream().map(Jar.class::cast).toList();

        ssgBuildModel.getBuildOutputLibs().addAll(
                ssgJarTasks.stream().flatMap(jar -> jar.getOutputs().getFiles().getFiles().stream()).toList()
        );

        ssgBuildModel.getRuntimeClasspath().addAll(
                project.getConfigurations().named("runtimeClasspath").get().getFiles()
        );

        return ssgBuildModel;
    }

}
