package com.archiwork.aggregator.client;

import java.time.Instant;

public record Command(
        Long id,
        Instant commandDate,
        String mapId,
        String mapKey,
        String mapValue
) {}
