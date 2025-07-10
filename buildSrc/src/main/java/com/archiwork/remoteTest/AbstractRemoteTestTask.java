package com.archiwork.remoteTest;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.testing.Test;

import java.util.Map;

public abstract class AbstractRemoteTestTask extends Test {

    private final Property<String> vaultUrlTerraformOutputName;
    private final MapProperty<String, String> keyVaultToEnvMappings;
    private final MapProperty<String, String> terraformToEnvMappings;
    private final MapProperty<String, String> terraformOutputs;

    public AbstractRemoteTestTask() {
        super();

        this.vaultUrlTerraformOutputName = getObjectFactory().property(String.class);
        this.keyVaultToEnvMappings = getObjectFactory().mapProperty(String.class, String.class);
        this.terraformToEnvMappings = getObjectFactory().mapProperty(String.class, String.class);
        this.terraformOutputs = getObjectFactory().mapProperty(String.class, String.class);

        useJUnitPlatform();
    }

    @Input
    public MapProperty<String, String> getTerraformToEnvMappings() {
        return terraformToEnvMappings;
    }

    @Input
    public Property<String> getVaultUrlTerraformOutputName() {
        return vaultUrlTerraformOutputName;
    }

    @Input
    public MapProperty<String, String> getKeyVaultToEnvMappings() {
        return keyVaultToEnvMappings;
    }

    @Input
    public MapProperty<String, String> getTerraformOutputs() {
        return terraformOutputs;
    }

    @TaskAction
    public void executeTests() {
        getLogger().lifecycle("Injecting Terraform output values as environment variables...");

        for (Map.Entry<String, String> entry : terraformToEnvMappings.get().entrySet()) {
            String terraformOutputName = entry.getKey();
            String envVarName = entry.getValue();
            String value = terraformOutputs.get().get(terraformOutputName);
            environment(envVarName, value);
            getLogger().info("{} = {}", envVarName, value);
        }

        if (keyVaultToEnvMappings.isPresent()) {
            String keyVaultUrl = terraformOutputs.get().get(this.getVaultUrlTerraformOutputName().get());
            SecretClient secretClient = new SecretClientBuilder()
                    .vaultUrl(keyVaultUrl)
                    .credential(new DefaultAzureCredentialBuilder().build())
                    .buildClient();

            for (Map.Entry<String, String> entry : keyVaultToEnvMappings.get().entrySet()) {
                String secretName = entry.getKey();
                String envVarName = entry.getValue();
                String value = secretClient.getSecret(secretName).getValue();

                environment(envVarName, value);
                getLogger().info("{} set", envVarName);
            }
        }

        super.executeTests();
    }
}
