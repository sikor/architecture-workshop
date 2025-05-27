package com.archiwork.perf;

import static us.abstracta.jmeter.javadsl.JmeterDsl.*;

import java.time.Duration;
import java.time.Instant;

import us.abstracta.jmeter.javadsl.core.TestPlanStats;

public class PerformanceTest {

    public static void main(String[] args) throws Exception {
        // Obtain the bearer token from your shared utility (commons module)
        String token = new AccessTokenProvider().getAccessToken();

        // Define the report directory (optional)
        String reportDir = System.getProperty("reportDir", "../jmeter-report");

        TestPlanStats stats = testPlan(
                threadGroup()
                        .rampToAndHold(10, Duration.ofSeconds(10), Duration.ofSeconds(10))
                        .rampToAndHold(10, Duration.ofSeconds(10), Duration.ofSeconds(10))
                        .children(
                                httpSampler("https://archiwork-events-wa.azurewebsites.net/commands")
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
