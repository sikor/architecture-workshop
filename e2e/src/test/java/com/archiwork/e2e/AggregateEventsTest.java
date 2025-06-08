package com.archiwork.e2e;

import com.archiwork.commons.restClient.AccessTokenProvider;
import com.archiwork.commons.restClient.ApiProperties;
import com.archiwork.commons.restClient.RestClientConfig;
import com.archiwork.launcher.AppLauncher;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.Matchers.*;

@SpringBootTest(properties = "spring.profiles.active=e2e", classes = RestClientConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AggregateEventsTest {

    private static final Logger logger = LoggerFactory.getLogger(AggregateEventsTest.class);

    @Autowired
    private AccessTokenProvider accessTokenProvider;

    @Autowired
    private ApiProperties apiProperties;

    private RequestSpecification givenEventsToken() {
        return givenToken(RestClientConfig.EVENTS);
    }

    private RequestSpecification givenAggregatorToken() {
        return givenToken(RestClientConfig.AGGREGATOR);
    }

    private RequestSpecification givenToken(String registrationId) {
        String token = accessTokenProvider.getToken(registrationId);
        return RestAssured.given()
                .auth().oauth2(token);
    }

    @BeforeAll
    void beforeAll() {
        AppLauncher.startAppsWithDependenciesIfNeededAndPossible(
                apiProperties.tokenUri(),
                apiProperties.requireEventsBaseUrl(),
                apiProperties.requireAggregatorBaseUrl());
    }


    @AfterAll
    public void afterAll() {
        AppLauncher.stopAppsWithDependenciesIfWereStarted();
    }

    @Test
    void testCommandAndStatsFlow() {
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

        Response postResponse = givenEventsToken()
                .header("Content-Type", "application/json")
                .body(payload)
                .post(apiProperties.requireEventsBaseUrl() + "/commands")
                .then()
                .statusCode(200)
                .extract()
                .response();

        givenAggregatorToken()
                .get(apiProperties.requireAggregatorBaseUrl() + "/map/stats")
                .then()
                .statusCode(200)
                .body(notNullValue());
    }
}
