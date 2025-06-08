package com.archiwork.launcher;

import com.archiwork.aggregator.AggregatorApplication;
import com.archiwork.events.EventsApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

public class AppLauncher {

    private static ConfigurableApplicationContext eventsContext;
    private static ConfigurableApplicationContext aggregatorContext;

    private static final Logger logger = LoggerFactory.getLogger(AppLauncher.class);

    public static void startAppsWithDependenciesIfNeededAndPossible(URL tokenUrl, URL eventsUrl, URL aggregatorUrl) {
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

    public static void stopAppsWithDependenciesIfWereStarted() {
        try {
            AppLauncher.stopApps();
        } catch (Exception e) {
            logger.error("Failed to stop apps", e);
        }
        DockerComposeLauncher.stop();
    }

    public static void startApps() {
        eventsContext = EventsApplication.createApplication()
                .properties(Env.loadEnvFromClasspath("events-local.env"))
                .properties("spring.flyway.locations=classpath:db/migration/events")
                .run();

        aggregatorContext = AggregatorApplication.createApplication()
                .properties(Env.loadEnvFromClasspath("aggregator-local.env"))
                .properties("spring.flyway.locations=classpath:db/migration/aggregator")
                .run();
    }

    public static void stopApps() {
        if (aggregatorContext != null) aggregatorContext.close();
        if (eventsContext != null) eventsContext.close();
    }




    private static boolean isTcpReachable(URL url) {
        String host = url.getHost();
        int port = url.getPort() != -1 ? url.getPort() : url.getDefaultPort();

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 2000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static boolean isLocalhost(URL url) {
        String host = url.getHost();
        return "localhost".equalsIgnoreCase(host) || "127.0.0.1".equals(host);
    }
}
