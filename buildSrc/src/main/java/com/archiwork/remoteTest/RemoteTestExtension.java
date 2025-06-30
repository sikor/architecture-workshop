package com.archiwork.remoteTest;// com.archiwork.remoteTest.RemoteTestExtension.java

import org.gradle.api.Action;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

import javax.inject.Inject;

public abstract class RemoteTestExtension {
    private final EnvironmentVariables environmentVariables;
    private final Property<String> iacProjectName;

    @Inject
    public RemoteTestExtension(ObjectFactory objects) {
        this.environmentVariables = objects.newInstance(EnvironmentVariables.class);
        this.iacProjectName = objects.property(String.class);
    }

    public EnvironmentVariables getEnvironmentVariables() {
        return environmentVariables;
    }

    public void environmentVariables(Action<? super EnvironmentVariables> action) {
        action.execute(environmentVariables);
    }

    public Property<String> getIacProjectName() {
        return iacProjectName;
    }
}
