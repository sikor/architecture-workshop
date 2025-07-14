package com.archiwork.remoteTest;

import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecOperations;

import javax.inject.Inject;

public abstract class AbstractDeployToAzureTask extends DefaultTask {

    @Inject
    protected abstract ExecOperations getExecOperations();

    @Input
    public abstract Property<String> getAppName();

    @Input
    public abstract Property<String> getResourceGroup();

    @InputFile
    public abstract RegularFileProperty getJarFile();

    private static String getAzCommand() {
        return System.getProperty("os.name").toLowerCase().contains("win") ? "az.cmd" : "az";
    }

    @TaskAction
    public void configureAndExecute() {
        getExecOperations().exec(spec -> spec.commandLine(
                getAzCommand(), "webapp", "deploy",
                "--name", getAppName().get(),
                "--resource-group", getResourceGroup().get(),
                "--src-path", getJarFile().get(),
                "--type", "zip")
        );
    }
}
