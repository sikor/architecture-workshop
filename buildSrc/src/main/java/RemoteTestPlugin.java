import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.attributes.Category;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskProvider;

import java.io.File;

public class RemoteTestPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        final RemoteTestExtension ext = project.getExtensions().create("remoteTest", RemoteTestExtension.class);

        // Create terraformOutputs configuration
        Configuration terraformOutputs = project.getConfigurations().create("terraformOutputs", config -> {
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

        // Register task
        TaskProvider<AbstractRemoteTestTask> remoteTestTask = project.getTasks().register(
                "remoteTest",
                AbstractRemoteTestTask.class,
                task -> {
                    task.setGroup("verification");
                    task.setDescription("Runs tests with environment configured via Terraform outputs");


                    task.getTerraformToEnvMappings().set(ext.getEnvironmentVariables().getMappings());

                    Provider<File> outputFile = terraformOutputs.getElements()
                            .map(files -> files.iterator().next().getAsFile());

                    task.getTerraformOutputsFile().set(outputFile);
                });
    }
}