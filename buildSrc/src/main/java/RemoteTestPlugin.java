import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.attributes.Category;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskProvider;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RemoteTestPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
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
                project.project(":infra"));

        // Register task
        TaskProvider<AbstractRemoteTestTask> remoteTestTask = project.getTasks().register("remoteTest", AbstractRemoteTestTask.class, task -> {
            task.setGroup("verification");
            task.setDescription("Runs tests with environment configured via Terraform outputs");

            Map<String, String> mappings = new HashMap<>();
            mappings.put("token_uri", "TOKEN_URI");
            mappings.put("e2e_client_id", "CLIENT_ID");
            mappings.put("e2e_client_secret", "CLIENT_SECRET");
            mappings.put("events_app_url", "EVENTS_API_BASE_URL");
            mappings.put("aggregator_app_url", "AGGREGATOR_API_BASE_URL");
            mappings.put("events_app_client_credentials_scope", "EVENTS_APP_SCOPE");
            mappings.put("aggregator_app_client_credentials_scope", "AGGREGATOR_APP_SCOPE");

            task.getTerraformToEnvMappings().set(mappings);

            Provider<File> outputFile = terraformOutputs.getElements()
                    .map(files -> files.iterator().next().getAsFile());

            task.getTerraformOutputsFile().set(outputFile);
        });
    }
}