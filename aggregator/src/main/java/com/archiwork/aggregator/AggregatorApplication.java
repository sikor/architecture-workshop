package com.archiwork.aggregator;

import com.archiwork.commons.Env;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

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
        createApplicationWithDefaultProperties().run(args);
    }

    public static SpringApplicationBuilder createApplicationWithDefaultProperties() {
        return new SpringApplicationBuilder(AggregatorApplication.class)
                .properties("spring.config.name=application-aggregator")
                .properties(Env.loadEnvFromClasspath("aggregator-local.env"));
    }
}
