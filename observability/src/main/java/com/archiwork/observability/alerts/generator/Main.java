package com.archiwork.observability.alerts.generator;

import com.archiwork.observability.alerts.AlertDefinition;
import com.archiwork.observability.alerts.AlertSeverity;
import com.archiwork.observability.alerts.GaugeAlertDefinition;
import com.archiwork.observability.alerts.Operator;


import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

/**
 * CLI entry point to generate alert definit`ions.
 * Usage:
 * java -cp ... com.archiwork.observability.generator.Main <generator-name> <output-file>
 * <p>
 * Supported generators:
 * - prometheus
 * - azure-monitor (future)
 */
public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: java Main <generator-name> <output-file>");
            System.exit(1);
        }

        String generatorName = args[0].toLowerCase();
        Path outputPath = Path.of(args[1]);

        List<AlertDefinition> alerts = List.of(
                new GaugeAlertDefinition(
                        "DB Authentication failed",
                        "db_auth_failed",
                        Operator.EQUALS,
                        1,
                        Duration.ofMinutes(1),
                        AlertSeverity.CRITICAL,
                        "Application failed to authenticated to the database"
                )
        );



        String yaml;
        switch (generatorName) {
            case "prometheus" -> {
                PrometheusYamlGenerator prometheusGen = new PrometheusYamlGenerator();
                yaml = prometheusGen.generate(alerts);
            }
            case "azure-monitor" -> {
                AzureTerraformGenerator azureGen = new AzureTerraformGenerator();
                yaml = azureGen.generate(alerts, null, null);
            }
            default -> {
                System.err.println("Unknown generator: " + generatorName);
                System.exit(2);
                return;
            }
        }

        // Ensure directory exists
        Files.createDirectories(outputPath.getParent());
        Files.writeString(outputPath, yaml);

        System.out.println(generatorName + " alerts definition generated at: " + outputPath);
    }
}
