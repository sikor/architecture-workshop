package com.archiwork.commons.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class CommonMetrics {
    private final AtomicLong dbAuthFailed = new AtomicLong(0);
    private final MeterRegistry meterRegistry;

    public CommonMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        Gauge.builder("db.auth.failed", dbAuthFailed::get)
                .description("-1 = db auth succeeded, 0 = unknown, 1 = db auth failed")
                .baseUnit("is failed")
                .register(meterRegistry);
    }

    public void dbAuthFailed() {
        dbAuthFailed.set(1);
    }

    public void dbAuthSucceeded() {
        dbAuthFailed.set(-1);
    }

}