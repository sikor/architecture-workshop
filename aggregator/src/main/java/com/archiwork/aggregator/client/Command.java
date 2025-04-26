package com.archiwork.aggregator.client;

import java.time.Instant;

public record Command(
        String mapId,
        String mapKey,
        String mapValue,
        Instant commandDate
) {}
