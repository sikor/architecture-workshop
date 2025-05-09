package com.archiwork.events.cursors.right;

import com.archiwork.commons.ReliableTask;
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
public class PostgresPubSub extends ReliableTask {

    private static final Logger log = LoggerFactory.getLogger(PostgresPubSub.class);

    private final DataSource dataSource;
    private final ApplicationEventPublisher eventPublisher;

    public PostgresPubSub(DataSource dataSource, ApplicationEventPublisher eventPublisher) {
        super("PostgresPubSub");
        this.dataSource = dataSource;
        this.eventPublisher = eventPublisher;
    }

    public void publish(String message) {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("NOTIFY newCommands, '" + message.replace("'", "''") + "'");
        } catch (SQLException e) {
            log.error("Error publishing NOTIFY message", e);
        }
    }

    @Override
    protected void runWithResources() throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            PGConnection pgConn = conn.unwrap(PGConnection.class);
            stmt.execute("LISTEN newCommands");
            log.info("Connected and LISTENING on channel 'newCommands'");

            while (isRunning()) {
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
}
