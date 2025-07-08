package com.archiwork.observability.alerts;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class AlertDsl {
    private final List<AlertDefinition> definitions = new ArrayList<>();

    public void alert(String name, String metric, double threshold, Duration duration, AlertSeverity severity, String description) {
        definitions.add(new AlertDefinition(name, metric, threshold, duration, severity, description));
    }

    public List<AlertDefinition> getDefinitions() {
        return definitions;
    }
}
