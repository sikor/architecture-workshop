package com.archiwork.commons.restClient;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.swing.text.html.Option;
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

    public ApiBaseUrl requireAggregatorApi() {
        return Optional.ofNullable(aggregator)
                .orElseThrow(() -> new IllegalArgumentException("Aggregator api url must be provided in spring config"));
    }

    public Optional<ApiBaseUrl> getEventsUrl() {
        return Optional.ofNullable(events);
    }

    public Optional<ApiBaseUrl> getAggregatorUrl() {
        return Optional.ofNullable(aggregator);
    }
}

