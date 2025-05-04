package com.archiwork.events.cursors.right;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.function.Consumer;

@Component
public class PostgresPubSub {

    private final DataSource dataSource;
    private final Consumer<String> onNewCommand;
    private volatile boolean running = true;
    private Connection listenConnection;

    public PostgresPubSub(DataSource dataSource, Consumer<String> onNewCommand) {
        this.dataSource = dataSource;
        this.onNewCommand = onNewCommand;
    }

    @PostConstruct
    public void start() {
        Thread listenerThread = new Thread(this::listenLoop, "postgres-pubsub-listener");
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    @PreDestroy
    public void stop() {
        running = false;
        try {
            if (listenConnection != null && !listenConnection.isClosed()) {
                listenConnection.close();
            }
        } catch (SQLException ignored) {}
    }

    public void publish(String message) {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("NOTIFY newCommands, '" + message.replace("'", "''") + "'");
        } catch (SQLException e) {
            e.printStackTrace(); // Replace with logging
        }
    }

    private void listenLoop() {
        try {
            listenConnection = dataSource.getConnection();
            PGConnection pgConn = listenConnection.unwrap(PGConnection.class);

            try (Statement stmt = listenConnection.createStatement()) {
                stmt.execute("LISTEN newCommands");
            }

            // Real-time listening loop
            while (running) {
                // Wait for backend to send a message (this blocks!)
                PGNotification[] notifications = pgConn.getNotifications(1000);
                if (notifications != null) {
                    for (PGNotification notification : notifications) {
                        if ("newCommands".equals(notification.getName())) {
                            onNewCommand.accept(notification.getParameter());
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (running) e.printStackTrace(); // Replace with logging
        }
    }
}
