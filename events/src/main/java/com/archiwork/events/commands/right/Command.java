package com.archiwork.events.commands.right;

import java.time.LocalDateTime;

public record Command(
        LocalDateTime commandDate,
        String mapId,
        String mapKey,
        String mapValue
) {}