package com.archiwork.observability.alerts;

import java.time.Duration;

public sealed interface AlertDefinition permits GaugeAlertDefinition, CounterAlertDefinition {
    String name();

    String metric();

    Operator operator();

    double threshold();

    Duration forDuration();

    AlertSeverity severity();

    String description();
}
