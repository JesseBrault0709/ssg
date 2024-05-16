package com.jessebrault.ssg.gradle;

import groovy.lang.Writable;
import groovy.text.GStringTemplateEngine;
import groovy.text.Template;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.*;

import javax.inject.Inject;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SsgBinScriptTask extends DefaultTask {

    private final DirectoryProperty binDir;
    private final Property<String> scriptFileName;
    private final SetProperty<File> ssgJars;

    @Inject
    public SsgBinScriptTask(Project project, ObjectFactory objectFactory) {
        this.binDir = objectFactory.directoryProperty();
        this.binDir.convention(project.getLayout().getProjectDirectory().dir("bin"));
        this.scriptFileName = objectFactory.property(String.class);
        this.scriptFileName.convention("ssg");
        this.ssgJars = objectFactory.setProperty(File.class);
    }

    @OutputDirectory
    public DirectoryProperty getBinDir() {
        return this.binDir;
    }

    @Input
    public Property<String> getScriptFileName() {
        return this.scriptFileName;
    }

    @InputFiles
    public SetProperty<File> getSsgJars() {
        return this.ssgJars;
    }

    @TaskAction
    public void createBinScript() {
        final File binDir = this.getBinDir().get().getAsFile();
        //noinspection ResultOfMethodCallIgnored
        binDir.mkdirs();

        final var templateEngine = new GStringTemplateEngine();
        final Template template;
        try {
            template = templateEngine.createTemplate(this.getClass().getResource("binScriptTemplate.gst"));
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }

        final Map<String, String> binding = new HashMap<>();
        final String cp = this.getSsgJars().get().stream()
                .map(File::toString)
                .collect(Collectors.joining(":"));
        binding.put("cp", cp);

        final Writable writable = template.make(binding);

        final File target = new File(binDir, this.getScriptFileName().get());
        try (final OutputStream outputStream = new FileOutputStream(target)) {
            outputStream.write(writable.toString().getBytes());
            //noinspection ResultOfMethodCallIgnored
            target.setExecutable(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
