package com.archiwork.remoteTest;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.testing.Test;
import groovy.json.JsonSlurper;

import java.io.File;
import java.util.Map;

public abstract class AbstractRemoteTestTask extends Test {

    private final Property<String> vaultUrlTerraformOutputName;
    private final MapProperty<String, String> keyVaultToEnvMappings;
    private final Property<File> terraformOutputsFile;
    private final MapProperty<String, String> terraformToEnvMappings;

    public AbstractRemoteTestTask() {
        super();

        this.vaultUrlTerraformOutputName = getObjectFactory().property(String.class);
        this.keyVaultToEnvMappings = getObjectFactory().mapProperty(String.class, String.class);
        this.terraformOutputsFile = getObjectFactory().property(File.class);
        this.terraformToEnvMappings = getObjectFactory().mapProperty(String.class, String.class);

        useJUnitPlatform();
    }

    @Input
    public MapProperty<String, String> getTerraformToEnvMappings() {
        return terraformToEnvMappings;
    }

    @Input
    public Property<File> getTerraformOutputsFile() {
        return terraformOutputsFile;
    }

    @Input
    public Property<String> getVaultUrlTerraformOutputName() {
        return vaultUrlTerraformOutputName;
    }

    @Input
    public MapProperty<String, String> getKeyVaultToEnvMappings() {
        return keyVaultToEnvMappings;
    }

    @TaskAction
    public void executeTests() {
        System.out.println("📥 Injecting Terraform output values as environment variables...");

        File terraformOutputs = getTerraformOutputsFile().get();
        if (!terraformOutputs.exists()) {
            throw new RuntimeException("Terraform outputs file not found: " + terraformOutputs.getAbsolutePath());
        }

        // Parse using Groovy's built-in JSON parser
        Object tfOutputsParsed = new JsonSlurper().parse(terraformOutputs);

        if (!(tfOutputsParsed instanceof Map)) {
            throw new RuntimeException("Unexpected format in outputs.json");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> outputs = (Map<String, Object>) tfOutputsParsed;

        for (Map.Entry<String, String> entry : terraformToEnvMappings.get().entrySet()) {
            String terraformOutputName = entry.getKey();
            String envVarName = entry.getValue();
            String value = getTfOutputString(outputs, terraformOutputName);
            environment(envVarName, value);
            System.out.printf("✅ %s = %s%n", envVarName, value);
        }

        if (keyVaultToEnvMappings.isPresent()) {
            String keyVaultUrl = getTfOutputString(outputs, this.getVaultUrlTerraformOutputName().get());
            SecretClient secretClient = new SecretClientBuilder()
                    .vaultUrl(keyVaultUrl)
                    .credential(new DefaultAzureCredentialBuilder().build())
                    .buildClient();

            for (Map.Entry<String, String> entry : keyVaultToEnvMappings.get().entrySet()) {
                String secretName = entry.getKey();
                String envVarName = entry.getValue();
                String value = secretClient.getSecret(secretName).getValue();

                environment(envVarName, value);
                System.out.printf("✅ %s set%n", envVarName);
            }

        }

        super.executeTests();
    }

    private static String getTfOutputString(Map<String, Object> outputs, String terraformOutputName) {
        Object output = outputs.get(terraformOutputName);
        @SuppressWarnings("unchecked")
        String value = ((Map<String, String>) output).get("value");
        return value;
    }
}
