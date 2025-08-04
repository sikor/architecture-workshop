package com.archiwork.observability.alerts.generator;

import com.archiwork.observability.alerts.AlertDefinition;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AzureTerraformGenerator {

    private final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public String generate(List<AlertDefinition> alerts, String resourceGroupName, String appInsightsId) throws Exception {
        Map<String, Object> root = new HashMap<>();
        Map<String, Object> resources = new HashMap<>();

        for (AlertDefinition alert : alerts) {
            Map<String, Object> alertResource = new HashMap<>();
            alertResource.put("name", alert.name());
            alertResource.put("resource_group_name", resourceGroupName);
            alertResource.put("scopes", List.of(appInsightsId));
            alertResource.put("description", alert.description());
            alertResource.put("severity", mapSeverity(alert.severity()));
            alertResource.put("frequency", "PT1M");
            alertResource.put("window_size", formatDuration(alert.forDuration()));
            alertResource.put("enabled", true);

            Map<String, Object> criteria = new HashMap<>();
            criteria.put("metric_namespace", "customMetrics");
            criteria.put("metric_name", alert.metric());
            criteria.put("aggregation", "Total");
            criteria.put("operator", "GreaterThan");
            criteria.put("threshold", alert.threshold());
            alertResource.put("criteria", criteria);

            Map<String, Object> resourceType = new HashMap<>();
            resourceType.put(alert.name(), alertResource);

            resources.put("azurerm_monitor_metric_alert", resourceType);
        }

        root.put("resource", resources);

        return objectMapper.writeValueAsString(root);
    }

    private String formatDuration(Duration duration) {
        long minutes = duration.toMinutes();
        return "PT" + minutes + "M";
    }

    private int mapSeverity(com.archiwork.observability.alerts.AlertSeverity severity) {
        return switch (severity) {
            case CRITICAL -> 0;
            case ERROR -> 2;
            case WARN -> 3;
        };
    }
}
