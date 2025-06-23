

import org.gradle.api.GradleException;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.testing.Test;
import org.gradle.process.ExecOperations;
import org.ysb33r.gradle.terraform.TerraformSourceSet;
import org.ysb33r.gradle.terraform.extensions.TerraformExtension;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractRemoteTestTask extends Test {

    private final ExecOperations execOps;

    @Input
    public abstract MapProperty<String, String> getTerraformToEnvMappings();

    private final Map<String, Provider<String>> resolvedOutputs = new HashMap<>();

    @Inject
    public AbstractRemoteTestTask(ExecOperations execOps) {
        this.execOps = execOps;
        useJUnitPlatform();

        TerraformExtension terraform = getProject().getExtensions().findByType(TerraformExtension.class);
        if (terraform == null) {
            throw new GradleException("Terraform extension not found. Did you apply the Terraform plugin?");
        }

        TerraformSourceSet sourceSet = terraform.getSourceSets().getByName("main");

        for (Map.Entry<String, String> entry : getTerraformToEnvMappings().get().entrySet()) {
            String terraformOutputName = entry.getKey();
            String envVarName = entry.getValue();
            resolvedOutputs.put(envVarName, sourceSet.rawOutputVariable(terraformOutputName).map(Object::toString));
        }
    }

    @TaskAction
    public void executeTests() {
        // Inject environment variables
        System.out.println("📥 Injecting Terraform output values as environment variables...");
        for (Map.Entry<String, Provider<String>> entry : resolvedOutputs.entrySet()) {
            String envVarName = entry.getKey();
            String value = entry.getValue().get();
            environment(envVarName, value);
            System.out.printf("✅ %s = %s%n", envVarName, value);
        }

        super.executeTests();
    }

    private String execAndCapture(String[] command, File workingDir) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        execOps.exec(spec -> {
            spec.setWorkingDir(workingDir);
            spec.commandLine((Object[]) command);
            spec.setIgnoreExitValue(true);
            spec.setStandardOutput(output);
            spec.setErrorOutput(output);
        });
        return output.toString().trim();
    }
}
