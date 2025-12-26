package com.archiwork.commons.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class Env {

    private static final Logger logger = LoggerFactory.getLogger(Env.class);

    public static Properties loadEnvFromClasspath(String resourceName) {
        Properties props = new Properties();
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName)) {
            if (in == null) {
                logger.info("Could not find env file on classpath: {}", resourceName);
                return props;
            }
            props.load(in);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load env file: " + resourceName, e);
        }
        return props;
    }
}
