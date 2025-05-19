package com.archiwork.e2e.utils;

import com.archiwork.aggregator.AggregatorApplication;
import com.archiwork.events.EventsApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class AppLauncher {

    private static ConfigurableApplicationContext eventsContext;
    private static ConfigurableApplicationContext aggregatorContext;

    public static void startApps() {
        eventsContext = new SpringApplicationBuilder(EventsApplication.class)
            .profiles("e2e") // optional: use a test profile
            .run();

        aggregatorContext = new SpringApplicationBuilder(AggregatorApplication.class)
            .profiles("e2e")
            .run();
    }

    public static void stopApps() {
        if (aggregatorContext != null) aggregatorContext.close();
        if (eventsContext != null) eventsContext.close();
    }
}
