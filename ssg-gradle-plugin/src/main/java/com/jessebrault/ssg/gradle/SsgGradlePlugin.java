package com.jessebrault.ssg.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.plugins.GroovyPlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.*;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.compile.GroovyCompile;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.tooling.provider.model.ToolingModelBuilderRegistry;

import javax.inject.Inject;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SsgGradlePlugin implements Plugin<Project> {

    public static final String TASK_GROUP = "ssg";
    public static final String COMPONENTS_SOURCE_SET = "components";
    public static final String PAGES_SOURCE_SET = "pages";
    public static final String SSG_SOURCE_SET = "ssg";

    private final ToolingModelBuilderRegistry toolingModelBuilderRegistry;

    @Inject
    public SsgGradlePlugin(ToolingModelBuilderRegistry toolingModelBuilderRegistry) {
        this.toolingModelBuilderRegistry = toolingModelBuilderRegistry;
    }

    protected void configureRepositories(Project project) {
        // add tooling api repository because ssg-cli depends on it
        project.getRepositories().maven(mavenRepository -> {
            mavenRepository.setName("gradleToolingApiRepository");
            mavenRepository.setUrl("https://repo.gradle.org/gradle/libs-releases");
        });
    }

    protected void configureToolingModelBuilders() {
        this.toolingModelBuilderRegistry.register(new SsgBuildModelBuilder());
    }

    protected Configuration createSsgApiConfiguration(Project project) {
        final Configuration ssgApiConfiguration = project.getConfigurations().create("ssgApi");
        ssgApiConfiguration.setCanBeConsumed(false);
        ssgApiConfiguration.setCanBeResolved(true); // N.B.: Only a dependency container
        ssgApiConfiguration.setVisible(false);
        return ssgApiConfiguration;
    }

    protected Configuration createSsgCliConfiguration(Project project, Configuration ssgApiConfiguration) {
        final Configuration ssgCliConfiguration = project.getConfigurations().create("ssgCli");
        ssgCliConfiguration.setCanBeConsumed(false);
        ssgCliConfiguration.setCanBeResolved(true);
        ssgCliConfiguration.setVisible(false);
        ssgCliConfiguration.extendsFrom(ssgApiConfiguration);
        return ssgCliConfiguration;
    }

    protected void createDomainSourceSet(
            Project project,
            SourceSetContainer sourceSets,
            String name,
            List<String> sourceSetDependencies
    ) {
        sourceSets.create(name, sourceSet -> {
            // first, register the dirs
            // java
            final var javaSourceDirectorySet = sourceSet.getJava();
            javaSourceDirectorySet.setSrcDirs(List.of(name + File.separator + "java"));

            // groovy
            final var groovySourceDirectorySet = sourceSet.getExtensions().getByType(GroovySourceDirectorySet.class);
            groovySourceDirectorySet.setSrcDirs(List.of(name + File.separator + "groovy"));

            // resources
            sourceSet.getResources().setSrcDirs(List.of(name + File.separator + "resources"));

            // second, configure the relevant compile tasks
            project.getTasks().named(sourceSet.getCompileJavaTaskName(), JavaCompile.class, javaCompile -> {
                javaCompile.source(name + File.separator + "java");
            });
            project.getTasks().named(sourceSet.getCompileTaskName("groovy"), GroovyCompile.class, groovyCompile -> {
                groovyCompile.source(name + File.separator + "groovy");
            });

            // third, we need a jar task which knows where stuff is
            final TaskProvider<Jar> jarTaskProvider = project.getTasks().register(
                    sourceSet.getJarTaskName(),
                    Jar.class,
                    jarTask -> {
                        jarTask.getExtensions().create("ssgJar", SsgJarExtension.class);
                        // jarTask.from(javaSourceDirectorySet.getClassesDirectory()); // TODO: why not?
                        jarTask.from(groovySourceDirectorySet.getClassesDirectory());
                        jarTask.from(sourceSet.getResources());
                        jarTask.getArchiveBaseName().set(project.getName() + "-" + name);
                    }
            );

            project.getTasks().named("ssgJars", ssgJarsTask -> {
                ssgJarsTask.dependsOn(jarTaskProvider);
            });

            // fourth, configure dependencies on other source sets
            project.getConfigurations().named(name + "Implementation").configure(configuration -> {
                sourceSetDependencies.forEach(dependencySourceSetName -> {
                    final SourceSetOutput sourceSetOutput =
                            sourceSets.getByName(dependencySourceSetName).getOutput();
                    final Dependency outputDependency = project.getDependencies().create(sourceSetOutput);
                    configuration.getDependencies().add(outputDependency);
                });
            });
        });
    }

    protected void createSsgSourceSet(Project project, SourceSetContainer sourceSets) {
        sourceSets.create(SSG_SOURCE_SET, sourceSet -> {
            // groovy only
            sourceSet.getExtensions().getByType(GroovySourceDirectorySet.class).setSrcDirs(List.of(SSG_SOURCE_SET));
            // resources live right next to build scripts, if needed
            sourceSet.getResources().setSrcDirs(List.of(SSG_SOURCE_SET));
            // disable java
            sourceSet.getJava().setSrcDirs(List.of());
            // add dependency on main
            final SourceSetOutput mainOutput = sourceSets.getByName("main").getOutput();
            project.getDependencies().add(SSG_SOURCE_SET + "Implementation", mainOutput);
        });
    }

    protected void configureSourceSets(Project project) {
        final var javaExtension = project.getExtensions().getByType(JavaPluginExtension.class);
        final var sourceSets = javaExtension.getSourceSets();
        this.createSsgSourceSet(project, sourceSets);
        this.createDomainSourceSet(project, sourceSets, COMPONENTS_SOURCE_SET, List.of("main"));
        this.createDomainSourceSet(project, sourceSets, PAGES_SOURCE_SET, List.of("main", COMPONENTS_SOURCE_SET));
    }

    protected void configureSourceSetConfigurations(Project project, Configuration ssgApiConfiguration) {
        final ConfigurationContainer configurations = project.getConfigurations();

        final Configuration implementation = configurations.getByName("implementation");
        implementation.extendsFrom(ssgApiConfiguration);

        final Set<Configuration> subConfigurations = new HashSet<>();
        subConfigurations.add(configurations.getByName("ssgImplementation"));
        subConfigurations.add(configurations.getByName("pagesImplementation"));
        subConfigurations.add(configurations.getByName("componentsImplementation"));

        subConfigurations.forEach(subConfiguration -> subConfiguration.extendsFrom(implementation));
    }

    protected void configureSsgDependencies(
            Project project,
            Configuration ssgApiConfiguration,
            Configuration ssgCliConfiguration
    ) {
        final Dependency ssgApi = project.getDependencies().create("com.jessebrault.ssg:ssg-api:0.4.2");
        final Dependency ssgCli = project.getDependencies().create("com.jessebrault.ssg:ssg-cli:0.4.2");
        ssgApiConfiguration.getDependencies().add(ssgApi);
        ssgCliConfiguration.getDependencies().add(ssgCli);
    }

    @Override
    public void apply(Project project) {
        // apply the java and groovy plugins
        project.getPlugins().apply(JavaPlugin.class);
        project.getPlugins().apply(GroovyPlugin.class);

        // create our ssgJars task, which is just a holder for source set jar tasks, including main
        final TaskProvider<Task> ssgJars = project.getTasks().register("ssgJars");
        final TaskProvider<Jar> mainJar = project.getTasks().named("jar", Jar.class, task -> {
            task.getExtensions().create("ssgJar", SsgJarExtension.class);
        });
        ssgJars.configure(task -> task.dependsOn(mainJar));

        // configure the repositories, tooling models, and source sets
        this.configureRepositories(project);
        this.configureToolingModelBuilders();
        this.configureSourceSets(project);
        // this.configureCompileSsgGroovyTask(project);

        // configurations
        final Configuration ssgApiConfiguration = this.createSsgApiConfiguration(project);
        final Configuration ssgCliConfiguration = this.createSsgCliConfiguration(project, ssgApiConfiguration);
        this.configureSourceSetConfigurations(project, ssgApiConfiguration);

        // Tasks
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

        tasks.register("cleanSsg", Delete.class, deleteTask -> {
            deleteTask.delete(binScriptTaskTaskProvider.map(SsgBinScriptTask::getBinDir));
            deleteTask.delete(".ssg");
            deleteTask.setGroup(TASK_GROUP);
        });

        project.afterEvaluate(p -> this.configureSsgDependencies(p, ssgApiConfiguration, ssgCliConfiguration));
    }

}
