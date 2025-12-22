package com.archiwork.observability.alerts;

import java.time.Duration;

public record GaugeAlertDefinition(String name,
                                   String metric,
                                   Operator operator,
                                   double threshold,
                                   Duration forDuration,
                                   AlertSeverity severity,
                                   String description) implements AlertDefinition {
}
