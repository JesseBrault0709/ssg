package com.jessebrault.ssg.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.Delete;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.tooling.provider.model.ToolingModelBuilderRegistry;

import javax.inject.Inject;
import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;

public class SsgGradlePlugin implements Plugin<Project> {

    public static final String TASK_GROUP = "ssg";

    private final ToolingModelBuilderRegistry toolingModelBuilderRegistry;

    @Inject
    public SsgGradlePlugin(ToolingModelBuilderRegistry toolingModelBuilderRegistry) {
        this.toolingModelBuilderRegistry = toolingModelBuilderRegistry;
    }

    protected void configureToolingModelBuilders() {
        this.toolingModelBuilderRegistry.register(new SsgBuildModelBuilder());
    }

    protected void configureSsgCliDependency(Project project) {
        final var ssgCliConfiguration = project.getConfigurations().getByName("ssgCli");
        final Dependency ssgCliDependency = project.getDependencies().create("com.jessebrault.ssg:ssg-cli:0.4.0");
        ssgCliConfiguration.getDependencies().add(ssgCliDependency);
    }

    @Override
    public void apply(Project project) {
        // apply at least java plugin
        project.getPlugins().apply(JavaPlugin.class);

        // add tooling api repository because ssg-cli depends on it
        project.getRepositories().maven(mavenRepository -> {
            mavenRepository.setName("gradle-tooling-api-repository");
            mavenRepository.setUrl("https://repo.gradle.org/gradle/libs-releases");
        });

        this.configureToolingModelBuilders();

        final Configuration ssgCliConfiguration = project.getConfigurations().create("ssgCli");
        ssgCliConfiguration.setCanBeConsumed(false);
        ssgCliConfiguration.setCanBeResolved(true);
        ssgCliConfiguration.setVisible(false);

        final TaskContainer tasks = project.getTasks();

        final TaskProvider<SsgBinScriptTask> binScriptTaskTaskProvider = tasks.register(
                "createSsgBinScript",
                SsgBinScriptTask.class,
                ssgBinScriptTask -> {
                    final Set<File> dependencyJars = ssgCliConfiguration.getFiles().stream()
                            .filter(file -> file.getName().endsWith(".jar"))
                            .collect(Collectors.toSet());
                    ssgBinScriptTask.getSsgJars().convention(dependencyJars);
                    ssgBinScriptTask.setGroup(TASK_GROUP);
                }
        );

        tasks.register("cleanBin", Delete.class, deleteTask -> {
            deleteTask.setDelete(binScriptTaskTaskProvider.map(SsgBinScriptTask::getBinDir));
            deleteTask.setGroup(TASK_GROUP);
        });

        project.afterEvaluate(this::configureSsgCliDependency);
    }

}
