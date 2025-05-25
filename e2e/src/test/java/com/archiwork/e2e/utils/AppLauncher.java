package com.archiwork.e2e.utils;

import com.archiwork.aggregator.AggregatorApplication;
import com.archiwork.events.EventsApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.InputStream;
import java.util.Properties;

public class AppLauncher {

    private static ConfigurableApplicationContext eventsContext;
    private static ConfigurableApplicationContext aggregatorContext;

    private static final Logger logger = LoggerFactory.getLogger(AppLauncher.class);

    public static void startApps() {
        eventsContext = EventsApplication.createApplication()
                .properties(loadEnvFromClasspath("events-local.env"))
                .properties("spring.flyway.locations=classpath:db/migration/events")
                .run();

        aggregatorContext = AggregatorApplication.createApplication()
                .properties(loadEnvFromClasspath("aggregator-local.env"))
                .properties("spring.flyway.locations=classpath:db/migration/aggregator")
                .run();
    }

    public static void stopApps() {
        if (aggregatorContext != null) aggregatorContext.close();
        if (eventsContext != null) eventsContext.close();
    }

    private static Properties loadEnvFromClasspath(String resourceName) {
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
