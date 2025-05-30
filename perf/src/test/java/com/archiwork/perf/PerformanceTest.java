package com.archiwork.perf;

import com.archiwork.commons.restClient.ApiProperties;
import com.archiwork.commons.restClient.RestClientConfig;
import com.archiwork.commons.restClient.AccessTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import us.abstracta.jmeter.javadsl.core.TestPlanStats;

import java.net.URL;
import java.time.Duration;
import java.time.Instant;

import static us.abstracta.jmeter.javadsl.JmeterDsl.*;

@SpringBootTest(
        properties = "spring.profiles.active=perf",
        classes = {RestClientConfig.class}
)
public class PerformanceTest {

    @Autowired
    private ApiProperties apiProperties;

    @Autowired
    private AccessTokenProvider tokenProvider;

    @Test
    void runPerformanceTest() throws Exception {
        String token = tokenProvider.getEventsToken();
        URL url = apiProperties.requireEventsApi().baseUrl();
        String reportDir = System.getProperty("reportDir", "../jmeter-report");


        TestPlanStats stats = testPlan(
                threadGroup()
                        .rampToAndHold(1, Duration.ofSeconds(10), Duration.ofSeconds(10))
                        .rampToAndHold(2, Duration.ofSeconds(10), Duration.ofSeconds(10))
                        .children(
                                httpSampler(url.toString() + "/commands")
                                        .method("POST")
                                        .header("Authorization", "Bearer " + token)
                                        .header("Content-Type", "application/json")
                                        .body(String.format("""
                      [
                        {
                          "commandDate": "%s",
                          "mapId": "loadtest-123",
                          "mapKey": "key-abc",
                          "mapValue": "value-xyz"
                        }
                      ]
                      """, Instant.now()))
                        ),
                jtlWriter(reportDir) // Logs details of each request
        ).run();



        System.out.println("✅ Performance test completed. JTL report available at: " + reportDir);
    }
}
