package com.archiwork.aggregator;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@OpenAPIDefinition(
        info = @Info(
                title = "Map Aggregator API",
                version = "1.0",
                description = "Map Aggregator API"
        )
)
@ConfigurationPropertiesScan(basePackages = {"com.archiwork.aggregator", "com.archiwork.commons"})
@SpringBootApplication(scanBasePackages = {"com.archiwork.aggregator", "com.archiwork.commons"})
public class AggregatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(AggregatorApplication.class, args);
    }
}
