package com.archiwork.events.cursors.right;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class PostgresPubSub {

    private static final Logger log = LoggerFactory.getLogger(PostgresPubSub.class);

    private final DataSource dataSource;
    private final ApplicationEventPublisher eventPublisher;
    private volatile boolean running = true;
    private Thread listenerThread;

    public PostgresPubSub(DataSource dataSource, ApplicationEventPublisher eventPublisher) {
        this.dataSource = dataSource;
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void start() {
        listenerThread = new Thread(this::listenLoopWithReconnect, "postgres-pubsub-listener");
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    @PreDestroy
    public void stop() {
        running = false;
        if (listenerThread != null) {
            listenerThread.interrupt();
        }
    }

    public void publish(String message) {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("NOTIFY newCommands, '" + message.replace("'", "''") + "'");
        } catch (SQLException e) {
            log.error("Error publishing NOTIFY message", e);
        }
    }

    private void listenLoopWithReconnect() {
        int retryDelay = 1000;
        while (running) {
            try {
                listenLoop();
                break;
            } catch (SQLException e) {
                if (!running) break;
                log.warn("PostgreSQL listen connection lost, retrying in {}ms", retryDelay, e);
                sleepSilently(retryDelay);
                retryDelay = Math.min(retryDelay * 2, 30_000);
            }
        }
    }

    private void listenLoop() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            PGConnection pgConn = conn.unwrap(PGConnection.class);
            stmt.execute("LISTEN newCommands");
            log.info("Connected and LISTENING on channel 'newCommands'");

            while (running) {
                PGNotification[] notifications = pgConn.getNotifications(5000);
                if (notifications != null) {
                    for (PGNotification notification : notifications) {
                        if ("newCommands".equals(notification.getName())) {
                            eventPublisher.publishEvent(new NewCommandEvent(notification.getParameter()));
                        }
                    }
                }
            }
        }
    }

    private void sleepSilently(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
