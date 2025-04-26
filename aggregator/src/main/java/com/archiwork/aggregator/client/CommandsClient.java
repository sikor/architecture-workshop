package com.archiwork.aggregator.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Component
public class CommandsClient {

    private final RestClient restClient;

    public CommandsClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<Command> getCommands(String sinceId, int limit) {
        ResponseEntity<List<Command>> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/commands")
                        .queryParam("serviceName", "aggregator")
                        .queryParam("sinceId", sinceId)
                        .queryParam("limit", limit)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        return response.getBody();
    }
}
