package com.archiwork.aggregator.client;

import com.archiwork.commons.restClient.EventsRestClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class CommandsClient {

    private final RestClient restClient;

    public CommandsClient(EventsRestClient restClient) {
        this.restClient = restClient.restClient();
    }

    public List<Command> acknowledgeCommands(String serviceName, long sinceId, int limit) {
        return restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/cursors")
                        .queryParam("serviceName", serviceName)
                        .queryParam("sinceId", sinceId)
                        .queryParam("limit", limit)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
