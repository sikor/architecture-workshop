package com.archiwork.perf;

import com.archiwork.commons.properties.DefaultPropertiesLoader;
import com.archiwork.commons.restClient.ApiProperties;
import com.archiwork.commons.restClient.RestClientConfig;
import com.archiwork.commons.restClient.AccessTokenProvider;
import com.archiwork.launcher.AppLauncher;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import us.abstracta.jmeter.javadsl.core.TestPlanStats;

import java.net.URL;
import java.time.Duration;
import java.time.Instant;

import static us.abstracta.jmeter.javadsl.JmeterDsl.*;
import static us.abstracta.jmeter.javadsl.core.listeners.AutoStopListener.AutoStopCondition.errors;

@SpringBootTest(
        properties = "spring.profiles.active=perf",
        classes = {RestClientConfig.class}
)
@ContextConfiguration(initializers = PerformanceTest.Initializer.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PerformanceTest {

    @Autowired
    private ApiProperties apiProperties;

    @Autowired
    private AccessTokenProvider tokenProvider;

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
    void runPerformanceTest() throws Exception {
        String token = tokenProvider.getEventsToken();
        URL url = apiProperties.requireEventsApi().baseUrl();
        String reportDir = System.getProperty("reportDir", "../jmeter-report");


        TestPlanStats stats = testPlan(
                httpDefaults()
                        .connectionTimeout(Duration.ofSeconds(1))
                        .responseTimeout(Duration.ofSeconds(1)),
                rpsThreadGroup()
                        .maxThreads(1000)
                        .rampToAndHold(5, Duration.ofSeconds(5), Duration.ofSeconds(10))
                        .rampToAndHold(10, Duration.ofSeconds(5), Duration.ofSeconds(10))
                        .rampToAndHold(15, Duration.ofSeconds(5), Duration.ofSeconds(10))
                        .rampToAndHold(20, Duration.ofSeconds(5), Duration.ofSeconds(10))
                        .rampToAndHold(25, Duration.ofSeconds(5), Duration.ofSeconds(10))
                        .rampToAndHold(30, Duration.ofSeconds(5), Duration.ofSeconds(10))
                        .rampToAndHold(35, Duration.ofSeconds(5), Duration.ofSeconds(10))
                        .rampToAndHold(40, Duration.ofSeconds(5), Duration.ofSeconds(10))
                        .rampToAndHold(45, Duration.ofSeconds(5), Duration.ofSeconds(10))
                        .rampToAndHold(50, Duration.ofSeconds(5), Duration.ofSeconds(10))
                        .children(
                                httpSampler("Commands sampler", url.toString() + "/commands")
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
                autoStop()
                        .when(errors().total().greaterThan(100L)), // when any sample fails, then test plan will stop and an exception will be thrown pointing to this condition.
                htmlReporter(reportDir).timeGraphsGranularity(Duration.ofSeconds(1)),
                jtlWriter(reportDir) // Logs details of each request
        ).run();


        System.out.println("✅ Performance test completed. JTL report available at: " + reportDir);
    }

    public static class Initializer extends DefaultPropertiesLoader {

        @Override
        protected String getConfigFileName() {
            return "perf-local.env";
        }
    }
}

