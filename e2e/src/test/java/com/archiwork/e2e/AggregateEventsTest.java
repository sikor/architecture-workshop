package com.archiwork.e2e;

import com.archiwork.e2e.utils.AppLauncher;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AggregateEventsTest {

    private static final String TOKEN = System.getenv("E2E_BEARER_TOKEN"); // or read via test config
    private static final String EVENTS_URL = "http://localhost:8080/commands"; // for local docker-compose
    private static final String AGGREGATOR_URL = "http://localhost:8081/map/stats";

    @Test
    void testCommandAndStatsFlow() {
        new AppLauncher();
        String payload = """
                [
                  {
                    "commandDate": "2025-05-19T18:11:51.714Z",
                    "mapId": "2",
                    "mapKey": "2",
                    "mapValue": "string"
                  }
                ]
                """;

        Response postResponse = given()
                .header("Authorization", "Bearer " + TOKEN)
                .header("Content-Type", "application/json")
                .body(payload)
                .post(EVENTS_URL)
                .then()
                .statusCode(200)
                .extract()
                .response();

        System.out.println("POST response: " + postResponse.asString());

        given()
                .get(AGGREGATOR_URL)
                .then()
                .statusCode(200)
                .body(notNullValue());
    }
}
