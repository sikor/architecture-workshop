package com.archiwork.commons.restClient;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.client.RestClient;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Configuration
@EnableConfigurationProperties(ApiProperties.class)
@ComponentScan(basePackages = {"com.archiwork.commons.restClient"})
public class RestClientConfig {

    public static final String EVENTS = "events";
    public static final String AGGREGATOR = "aggregator";

    private static ClientRegistration buildRegistration(ApiProperties props, ApiBaseUrl url, String id) {
        return ClientRegistration
                .withRegistrationId(id)
                .tokenUri(props.tokenUri().toString())
                .clientId(props.clientId())
                .clientSecret(props.clientSecret())
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope(url.scopes())
                .build();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(
            ApiProperties props) {
        Optional<ClientRegistration> eventsRegistration =
                props.getEventsUrl().map(url -> buildRegistration(props, url, EVENTS));
        Optional<ClientRegistration> aggregatorRegistration =
                props.getAggregatorUrl().map(url -> buildRegistration(props, url, AGGREGATOR));
        List<ClientRegistration> registrations =
                Stream.of(eventsRegistration, aggregatorRegistration).flatMap(Optional::stream).toList();
        return new InMemoryClientRegistrationRepository(registrations);
    }

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository) {

        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .clientCredentials()
                        .build();

        AuthorizedClientServiceOAuth2AuthorizedClientManager manager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                        clientRegistrationRepository,
                        new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository)
                );

        manager.setAuthorizedClientProvider(authorizedClientProvider);

        return manager;
    }

    @Bean
    @ConditionalOnProperty(name = "archiwork-commons.api-access." + EVENTS + ".base-url")
    public EventsRestClient eventsRestClient(ApiProperties props,
                                             OAuth2AuthorizedClientManager authorizedClientManager) throws URISyntaxException {
        URL baseUrl = props.requireEventsApi().baseUrl();
        RestClient restClient = createRestClient(authorizedClientManager, EVENTS, baseUrl);
        return new EventsRestClient(restClient);
    }

    @Bean
    @ConditionalOnProperty(name = "archiwork-commons.api-access." + AGGREGATOR + ".base-url")
    public AggregatorRestClient aggregatorRestClient(ApiProperties props,
                                             OAuth2AuthorizedClientManager authorizedClientManager) throws URISyntaxException {
        URL baseUrl = props.requireAggregatorApi().baseUrl();
        RestClient restClient = createRestClient(authorizedClientManager, AGGREGATOR, baseUrl);
        return new AggregatorRestClient(restClient);
    }

    private static RestClient createRestClient(
            OAuth2AuthorizedClientManager authorizedClientManager,
            String name,
            URL baseUrl) throws URISyntaxException {
        OAuth2ClientHttpRequestInterceptor interceptor =
                new OAuth2ClientHttpRequestInterceptor(authorizedClientManager);
        interceptor.setClientRegistrationIdResolver(request -> name);
        return RestClient.builder()
                .baseUrl(baseUrl.toURI())
                .requestInterceptor(interceptor)
                .build();
    }
}