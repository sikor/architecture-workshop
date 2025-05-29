package com.archiwork.commons.restClient;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Component;

@Component
public class AccessTokenProvider {

    private static final Authentication ANONYMOUS_AUTHENTICATION = new AnonymousAuthenticationToken("anonymous",
            "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

    private final OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;

    public AccessTokenProvider(OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager, ApiProperties apiProperties) {
        this.oAuth2AuthorizedClientManager = oAuth2AuthorizedClientManager;
    }

    public String getEventsToken() {
        return getToken(RestClientConfig.EVENTS);
    }

    public String getAggregatesToken() {
        return getToken(RestClientConfig.AGGREGATOR);
    }

    public String getToken(String registrationId) {
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId(registrationId)
                .principal(ANONYMOUS_AUTHENTICATION)
                .build();
        return oAuth2AuthorizedClientManager.authorize(authorizeRequest)
                .getAccessToken().getTokenValue();
    }

}
