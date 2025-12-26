package com.archiwork.events;


import com.archiwork.commons.metrics.CommonMetrics;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.stereotype.Component;

@Component
class StartupFailureListener {

    private static final Logger logger = LoggerFactory.getLogger(StartupFailureListener.class);

    private final CommonMetrics commonMetrics;

    public StartupFailureListener(CommonMetrics commonMetrics) {
        this.commonMetrics = commonMetrics;
    }

    @EventListener
    void onFailure(ApplicationFailedEvent event) {
        logger.error("Startup failure listener");
        Throwable rootCause = NestedExceptionUtils.getRootCause(event.getException());
        if (rootCause instanceof PSQLException && rootCause.getCause().getMessage().contains("password authentication failed")) {
            commonMetrics.dbAuthFailed();
            logger.error("db auth failed registered");
        }
    }

    @EventListener
    void onReady(ApplicationReadyEvent e) {
        commonMetrics.dbAuthSucceeded();
    }
}