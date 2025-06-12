package com.archiwork.commons;


import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

public abstract class DefaultPropertiesLoader implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    protected abstract String getConfigFileName();

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        Properties env = Env.loadEnvFromClasspath(getConfigFileName());
        context.getEnvironment().getPropertySources().addLast(new PropertiesPropertySource("default-env", env));
    }
}
