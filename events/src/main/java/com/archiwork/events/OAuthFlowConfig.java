package com.archiwork.events;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "events.oauth-flow")
public record OAuthFlowConfig(String authorizationUrl, String tokenUrl, String scope) {
}
