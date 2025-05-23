package com.archiwork.commons.serverSecurity;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "archiwork-commons.oauth-flow")
public record OAuthFlowConfig(String authorizationUrl, String tokenUrl, String scope) {
}
