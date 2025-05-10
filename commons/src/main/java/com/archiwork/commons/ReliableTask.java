package com.archiwork.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public abstract class ReliableTask implements InitializingBean, DisposableBean {

    private final Thread thread;
    private volatile boolean running = true;
    private final Logger logger = LoggerFactory.getLogger(ReliableTask.class);

    protected ReliableTask(String threadName, boolean daemon) {
        this.thread = new Thread(this::run, threadName);
        thread.setDaemon(daemon);
    }

    private void run() {
        int retryDelay = 1000;
        while (running) {
            try {
                runWithResources(); // abstract method
                break;
            } catch (Exception e) {
                if (!running) break;
                logger.error("Listener failure: Retrying in: {}", retryDelay, e);
                sleep(retryDelay);
                retryDelay = Math.min(retryDelay * 2, 30_000);
            }
        }
    }

    protected boolean isRunning() {
        return running;
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    protected abstract void runWithResources() throws Exception;

    @Override
    public void afterPropertiesSet() throws Exception {
        thread.start();
    }

    @Override
    public void destroy() throws Exception {
        running = false;
        thread.interrupt();
    }
}
