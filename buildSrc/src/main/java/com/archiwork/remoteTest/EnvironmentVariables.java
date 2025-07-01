package com.archiwork.remoteTest;// TerraformOutputs.java

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.MapProperty;

import javax.inject.Inject;

public abstract class EnvironmentVariables {
    private final MapProperty<String, String> tfMappings;
    private final MapProperty<String, String> keyVaultMappings;

    @Inject
    public EnvironmentVariables(ObjectFactory objects) {
        this.tfMappings = objects.mapProperty(String.class, String.class);
        this.keyVaultMappings = objects.mapProperty(String.class, String.class);
    }

    public MapProperty<String, String> getTfMappings() {
        return tfMappings;
    }

    public void tfOutputToEnv(String terraformOutput, String envVar) {
        tfMappings.put(terraformOutput, envVar);
    }

    public MapProperty<String, String> getKeyVaultMappings() {
        return keyVaultMappings;
    }

    public void keyVaultToEnv(String secretName, String envVar) {
        tfMappings.put(secretName, envVar);
    }
}
