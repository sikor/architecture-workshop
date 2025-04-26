package com.archiwork.aggregator.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient commandsRestClient(@Value("${commands.api.base-url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}