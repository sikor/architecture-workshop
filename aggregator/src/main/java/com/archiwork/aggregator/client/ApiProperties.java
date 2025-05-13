package com.archiwork.aggregator.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "commands.api")
public record ApiProperties(
        String baseUrl,
        String tokenUri,
        String clientId,
        String clientSecret,
        List<String> scopes
) {}

