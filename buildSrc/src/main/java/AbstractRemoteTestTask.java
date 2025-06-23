import org.gradle.api.GradleException;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.testing.Test;
import org.ysb33r.gradle.terraform.TerraformSourceSet;
import org.ysb33r.gradle.terraform.extensions.TerraformExtension;

import java.util.Map;

public abstract class AbstractRemoteTestTask extends Test {

    private final MapProperty<String, String> terraformToEnvMappings;
    private final MapProperty<String, Object> outputVariables;

    public AbstractRemoteTestTask() {
        super();

        this.terraformToEnvMappings = getObjectFactory().mapProperty(String.class, String.class);
        this.outputVariables = getObjectFactory().mapProperty(String.class, Object.class);

        useJUnitPlatform();

        TerraformExtension terraform = getProject().getExtensions().findByType(TerraformExtension.class);
        if (terraform == null) {
            throw new GradleException("Terraform extension not found. Did you apply the Terraform plugin?");
        }

        TerraformSourceSet sourceSet = terraform.getSourceSets().getByName("main");
        this.outputVariables.set(sourceSet.rawOutputVariables());
    }

    @Input
    public MapProperty<String, String> getTerraformToEnvMappings() {
        return terraformToEnvMappings;
    }

    @Input
    public MapProperty<String, Object> getOutputVariables() {
        return outputVariables;
    }

    @TaskAction
    public void executeTests() {
        System.out.println("📥 Injecting Terraform output values as environment variables...");
        for (Map.Entry<String, String> entry : terraformToEnvMappings.get().entrySet()) {
            String terraformOutputName = entry.getKey();
            String envVarName = entry.getValue();
            Object value = outputVariables.get().get(terraformOutputName);
            environment(envVarName, value);
            System.out.printf("✅ %s = %s%n", envVarName, value);
        }

        super.executeTests();
    }
}
