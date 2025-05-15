package com.archiwork.events;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;


@OpenAPIDefinition(
        info = @Info(
                title = "Commands Queue API",
                version = "1.0",
                description = "Commands Queue API"
        )
)
@ConfigurationPropertiesScan(basePackages = {"com.archiwork.events", "com.archiwork.commons"})
@SpringBootApplication(scanBasePackages = {"com.archiwork.events", "com.archiwork.commons"})
public class EventsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventsApplication.class, args);
    }
}
