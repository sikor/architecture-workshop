package com.archiwork.observability.alerts;

import java.time.Duration;

public record CounterAlertDefinition(String name,
                                     String metric,
                                     Operator operator,
                                     double threshold,
                                     Duration forDuration,
                                     AlertSeverity severity,
                                     String description,
                                     Duration aggregationDuration) implements AlertDefinition {
}
