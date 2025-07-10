package com.archiwork.remoteTest;

import groovy.json.JsonSlurper;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public abstract class RemoteTestExtension {
    private final EnvironmentVariables environmentVariables;
    private final Property<String> iacProjectName;
    private final Property<String> keyVaultUrlTfOutputName;
    private final Property<String> resourceGroupTfOutputName;
    private final Property<String> appNameTfOutputName;
    private final Property<File> terraformOutputsFile;
    private final MapProperty<String, String> terraformOutputs;
    private final Property<AbstractArchiveTask> deploymentArchiveTask;

    @Inject
    public RemoteTestExtension(ObjectFactory objects, Project project) {
        this.environmentVariables = objects.newInstance(EnvironmentVariables.class);
        this.iacProjectName = objects.property(String.class);
        this.keyVaultUrlTfOutputName = objects.property(String.class);
        this.resourceGroupTfOutputName = objects.property(String.class);
        this.appNameTfOutputName = objects.property(String.class);
        this.terraformOutputsFile = objects.property(File.class);
        this.deploymentArchiveTask = objects.property(AbstractArchiveTask.class);
        this.terraformOutputs = objects.mapProperty(String.class, String.class);
        terraformOutputs.set(terraformOutputsFile.map(outputsFile -> parseOutputsFile(project, outputsFile)));
    }

    public EnvironmentVariables getEnvironmentVariables() {
        return environmentVariables;
    }

    public void environmentVariables(Action<? super EnvironmentVariables> action) {
        action.execute(environmentVariables);
    }

    public Property<String> getIacProjectName() {
        return iacProjectName;
    }

    public Property<String> getKeyVaultUrlTfOutputName() {
        return keyVaultUrlTfOutputName;
    }

    public Property<String> getResourceGroupTfOutputName() {
        return resourceGroupTfOutputName;
    }

    public Property<String> getAppNameTfOutputName() {
        return appNameTfOutputName;
    }

    public Property<File> getTerraformOutputsFile() {
        return terraformOutputsFile;
    }

    public MapProperty<String, String> getTerraformOutputs() {
        return terraformOutputs;
    }

    public Property<AbstractArchiveTask> getDeploymentArchiveTask() {
        return deploymentArchiveTask;
    }

    private static @NotNull HashMap<String, String> parseOutputsFile(Project project, File outputsFile) {
        project.getLogger().lifecycle("terraform outputs file: " + outputsFile.getAbsolutePath());
        if (!outputsFile.exists()) {
            throw new RuntimeException("Terraform outputs file not found: " + outputsFile.getAbsolutePath());
        }

        // Parse using Groovy's built-in JSON parser
        Object tfOutputsParsed = new JsonSlurper().parse(outputsFile);

        if (!(tfOutputsParsed instanceof Map)) {
            throw new RuntimeException("Unexpected format in outputs.json");
        }

        @SuppressWarnings("unchecked") final Map<String, Object> outputs = (Map<String, Object>) tfOutputsParsed;

        var map = new HashMap<String, String>();
        for (Map.Entry<String, Object> entry : outputs.entrySet()) {
            String terraformOutputName = entry.getKey();
            Object output = entry.getValue();
            @SuppressWarnings("unchecked")
            String value = ((Map<String, String>) output).get("value");
            map.put(terraformOutputName, value);
            System.out.printf("%s = %s%n", terraformOutputName, value);
        }
        return map;
    }
}
