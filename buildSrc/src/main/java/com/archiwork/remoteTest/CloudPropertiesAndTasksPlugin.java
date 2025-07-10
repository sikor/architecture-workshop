package com.archiwork.remoteTest;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.attributes.Category;
import org.gradle.api.file.RegularFile;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;

public class CloudPropertiesAndTasksPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        final RemoteTestExtension ext = project.getExtensions().create("remoteTest", RemoteTestExtension.class);

        // Create terraformOutputs configuration
        Configuration terraformOutputs =
                project.getConfigurations().create("terraformOutputs", config -> {
                    config.setCanBeConsumed(false);
                    config.setCanBeResolved(true); // Required to resolve files
                    config.attributes(aConfig -> {
                        aConfig.attribute(
                                Category.CATEGORY_ATTRIBUTE,
                                project.getObjects().named(Category.class, Category.LIBRARY));
                    });
                });


        project.getDependencies().add(terraformOutputs.getName(),
                ext.getIacProjectName().map(project::project));

        ext.getTerraformOutputsFile().set(terraformOutputs.getElements()
                .map(files -> files.iterator().next().getAsFile()));

        // Register task
        project.getTasks().register(
                "remoteTest",
                AbstractRemoteTestTask.class,
                task -> {
                    task.setGroup("verification");
                    task.setDescription("Runs tests with environment configured via Terraform outputs");
                    task.getVaultUrlTerraformOutputName().set(ext.getKeyVaultUrlTfOutputName());
                    task.getKeyVaultToEnvMappings().set(ext.getEnvironmentVariables().getKeyVaultMappings());
                    task.getTerraformToEnvMappings().set(ext.getEnvironmentVariables().getTfMappings());
                    task.getTerraformOutputs().set(ext.getTerraformOutputs());
                });


        Provider<String> appName = ext.getTerraformOutputs()
                .map(outputs -> outputs.get(ext.getAppNameTfOutputName().get()));
        Provider<String> resourceGroup = ext.getTerraformOutputs()
                .map(outputs -> outputs.get(ext.getResourceGroupTfOutputName().get()));
        Provider<RegularFile> jarFile = ext.getDeploymentArchiveTask().flatMap(AbstractArchiveTask::getArchiveFile);

        project.getTasks().register(
                "deployToAzure",
                AbstractDeployToAzureTask.class,
                task -> {
                    task.setGroup("deployment");
                    task.setDescription("Deploys the app to Azure App Service");
                    task.getAppName().set(appName);
                    task.getResourceGroup().set(resourceGroup);
                    task.getJarFile().set(jarFile);
                });
    }
}