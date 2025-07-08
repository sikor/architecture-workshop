package com.archiwork.observability.alerts;

import java.time.Duration;

public record AlertDefinition(String name,
                              String metric,
                              double threshold,
                              Duration duration,
                              AlertSeverity severity,
                              String description) {
}
