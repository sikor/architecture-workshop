package com.archiwork.observability.alerts;

import java.time.Duration;
import java.util.Optional;

public record AlertDefinition(String name,
                              MetricType metricType,
                              String metric,
                              Operator operator,
                              double threshold,
                              Duration forDuration,
                              AlertSeverity severity,
                              String description,
                              Optional<Duration> aggregationDuration) {
}
