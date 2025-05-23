package com.archiwork.commons.restClient;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Optional;

@ConfigurationProperties(prefix = "archiwork-commons.api-access")
public record ApiProperties(
        String tokenUri,
        String clientId,
        String clientSecret,
        ApiBaseUrl events,
        ApiBaseUrl aggregator
) {
    public ApiBaseUrl requireEventsApi() {
        return Optional.ofNullable(events)
                .orElseThrow(() -> new IllegalArgumentException("Events api url must be provided in spring config"));
    }
}

