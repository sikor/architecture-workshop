package com.archiwork.aggregator.map;

import java.time.Instant;

public record MapValue(
        Instant commandDate,
        String mapId,
        String mapKey,
        String mapValue
) {}
