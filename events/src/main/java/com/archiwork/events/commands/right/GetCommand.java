package com.archiwork.events.commands.right;

import java.time.Instant;

public record GetCommand(
        Long id,
        Instant commandDate,
        String mapId,
        String mapKey,
        String mapValue
) {}