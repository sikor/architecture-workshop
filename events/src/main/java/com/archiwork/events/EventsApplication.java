package com.archiwork.events;

import com.archiwork.commons.Env;
import com.archiwork.observability.metrics.MetricRegistry;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;


@OpenAPIDefinition(
        info = @Info(
                title = "Commands Queue API",
                version = "1.0",
                description = "Commands Queue API"
        )
)
@ConfigurationPropertiesScan(basePackages = {"com.archiwork.events", "com.archiwork.commons.serverSecurity"})
@SpringBootApplication(scanBasePackages = {"com.archiwork.events", "com.archiwork.commons.serverSecurity"})
public class EventsApplication {
    private static final Logger logger = LoggerFactory.getLogger(EventsApplication.class);

    public static void main(String[] args) {
        logger.info("Starting EventsApplication Spring application");
//        MetricRegistry metrics = new MetricRegistry(GlobalOpenTelemetry.getMeter("your-app"));
//        metrics.dbAuthFailures.add(1);

        createApplication().run(args);
        logger.info("Spring application started");
    }

    public static SpringApplicationBuilder createApplication() {
        return new SpringApplicationBuilder(EventsApplication.class)
                .properties("spring.config.name=application-events")
                .properties(Env.loadEnvFromClasspath("events-local.env"))
                .properties("spring.flyway.locations=classpath:db/migration/events");
    }
}
