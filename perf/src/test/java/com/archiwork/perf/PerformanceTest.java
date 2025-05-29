package com.archiwork.perf;

import com.archiwork.commons.restClient.RestClientConfig;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(properties = "spring.profiles.active=perf", classes = RestClientConfig.class)
public class PerformanceTest {

}
