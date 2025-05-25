package com.archiwork.e2e;

import com.archiwork.commons.restClient.ApiProperties;
import com.archiwork.commons.restClient.RestClientConfig;
import com.archiwork.e2e.utils.AppLauncher;
import com.archiwork.e2e.utils.DockerComposeLauncher;
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
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

import static org.hamcrest.Matchers.*;

@SpringBootTest(properties = "spring.profiles.active=e2e", classes = RestClientConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AggregateEventsTest {

    private static final Logger logger = LoggerFactory.getLogger(AggregateEventsTest.class);

    private static final Authentication ANONYMOUS_AUTHENTICATION = new AnonymousAuthenticationToken("anonymous",
            "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

    @Autowired
    private OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;

    @Autowired
    private ApiProperties apiProperties;

    private RequestSpecification givenEventsToken() {
        return givenToken(RestClientConfig.EVENTS);
    }

    private RequestSpecification givenAggregatorToken() {
        return givenToken(RestClientConfig.AGGREGATOR);
    }

    private URL getEventsUrl() {
        return apiProperties.requireEventsApi().baseUrl();
    }

    private URL getAggregatorUrl() {
        return apiProperties.requireAggregatorApi().baseUrl();
    }

    private RequestSpecification givenToken(String registrationId) {
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId(registrationId)
                .principal(ANONYMOUS_AUTHENTICATION)
                .build();
        String token = oAuth2AuthorizedClientManager.authorize(authorizeRequest)
                .getAccessToken().getTokenValue();
        return RestAssured.given()
                .auth().oauth2(token);
    }

    @BeforeAll
    void beforeAll() {
        URL tokenUrl = apiProperties.tokenUri();
        URL eventsUrl = getEventsUrl();
        URL aggregatorUrl = getAggregatorUrl();

        boolean tokenIsLocalhost = isLocalhost(tokenUrl);
        boolean eventsIsLocalhost = isLocalhost(eventsUrl);
        boolean aggregatorIsLocalhost = isLocalhost(aggregatorUrl);

        boolean tokensReachable = isTcpReachable(tokenUrl);
        boolean eventsReachable = isTcpReachable(eventsUrl);
        boolean aggregatorReachable = isTcpReachable(aggregatorUrl);

        if (tokenIsLocalhost && !tokensReachable) {
            DockerComposeLauncher.start();
        }

        if (eventsIsLocalhost &&
                aggregatorIsLocalhost &&
                !eventsReachable &&
                !aggregatorReachable) {
            AppLauncher.startApps();
        }

    }

    private boolean isTcpReachable(URL url) {
        String host = url.getHost();
        int port = url.getPort() != -1 ? url.getPort() : url.getDefaultPort();

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 2000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean isLocalhost(URL url) {
        String host = url.getHost();
        return "localhost".equalsIgnoreCase(host) || "127.0.0.1".equals(host);
    }

    @AfterAll
    public void afterAll() {
        try {
            AppLauncher.stopApps();
        } catch (Exception e) {
            logger.error("Failed to stop apps", e);
        }
        DockerComposeLauncher.stop();
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
                .post(getEventsUrl() + "/commands")
                .then()
                .statusCode(200)
                .extract()
                .response();

        givenAggregatorToken()
                .get(getAggregatorUrl() + "/map/stats")
                .then()
                .statusCode(200)
                .body(notNullValue());
    }
}
