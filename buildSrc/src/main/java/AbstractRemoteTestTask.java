import org.gradle.api.provider.MapProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.testing.Test;
import groovy.json.JsonSlurper;

import java.io.File;
import java.util.Map;

public abstract class AbstractRemoteTestTask extends Test {

    private final MapProperty<String, String> terraformToEnvMappings;

    public AbstractRemoteTestTask() {
        super();

        this.terraformToEnvMappings = getObjectFactory().mapProperty(String.class, String.class);

        useJUnitPlatform();
    }

    @Input
    public MapProperty<String, String> getTerraformToEnvMappings() {
        return terraformToEnvMappings;
    }

    @TaskAction
    public void executeTests() {
        System.out.println("📥 Injecting Terraform output values as environment variables...");

        File outputsFile = getProject().getLayout().getBuildDirectory().file("terraform/outputs.json").get().getAsFile();
        if (!outputsFile.exists()) {
            throw new RuntimeException("Terraform outputs file not found: " + outputsFile.getAbsolutePath());
        }

        // Parse using Groovy's built-in JSON parser
        Object rawParsed = new JsonSlurper().parse(outputsFile);

        if (!(rawParsed instanceof Map)) {
            throw new RuntimeException("Unexpected format in outputs.json");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> outputs = (Map<String, Object>) rawParsed;

        for (Map.Entry<String, String> entry : terraformToEnvMappings.get().entrySet()) {
            String terraformOutputName = entry.getKey();
            String envVarName = entry.getValue();
            Object value = outputs.get(terraformOutputName);
            environment(envVarName, value);
            System.out.printf("✅ %s = %s%n", envVarName, value);
        }

        super.executeTests();
    }
}
