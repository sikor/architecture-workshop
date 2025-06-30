package com.archiwork.remoteTest;// TerraformOutputs.java
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.MapProperty;

import javax.inject.Inject;

public abstract class EnvironmentVariables {
    private final MapProperty<String, String> mappings;

    @Inject
    public EnvironmentVariables(ObjectFactory objects) {
        this.mappings = objects.mapProperty(String.class, String.class);
    }

    public MapProperty<String, String> getMappings() {
        return mappings;
    }

    public void tfOutputToEnv(String terraformOutput, String envVar) {
        mappings.put(terraformOutput, envVar);
    }
}
