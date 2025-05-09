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

    public List<Command> acknowledgeCommands(String serviceName, long sinceId, int limit) {
        ResponseEntity<List<Command>> response = restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/cursors")
                        .queryParam("serviceName", serviceName)
                        .queryParam("sinceId", sinceId)
                        .queryParam("limit", limit)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        return response.getBody();
    }
}
