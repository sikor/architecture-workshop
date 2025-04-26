package com.archiwork.events.commands.right;

import java.time.Instant;
import java.util.Optional;

public record AddCommand(
        Instant commandDate,
        String mapId,
        String mapKey,
        String mapValue
) {}