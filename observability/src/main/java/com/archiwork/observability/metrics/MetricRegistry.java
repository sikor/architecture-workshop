package com.archiwork.observability.metrics;

import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;

public class MetricRegistry {
    private final Meter meter;

    public final LongCounter dbAuthFailures;

    public MetricRegistry(Meter meter) {
        this.meter = meter;

        this.dbAuthFailures = meter
                .counterBuilder("db.auth.failures")
                .setDescription("Counts DB authentication failures")
                .setUnit("failures")
                .build();
    }
}