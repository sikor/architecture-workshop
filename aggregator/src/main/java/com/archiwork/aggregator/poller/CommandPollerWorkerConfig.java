package com.archiwork.aggregator.poller;

import com.archiwork.aggregator.client.CommandsClient;
import com.archiwork.aggregator.map.MapValueDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

@Configuration
public class CommandPollerWorkerConfig implements ApplicationContextInitializer<GenericApplicationContext> {

    @Value("${polling.workers:1}")
    private int numberOfWorkers;
    private final int limit;
    private final CommandsClient commandsClient;
    private final MapValueDao mapValueDao;

    public CommandPollerWorkerConfig(int limit, CommandsClient commandsClient, MapValueDao mapValueDao) {
        this.limit = limit;
        this.commandsClient = commandsClient;
        this.mapValueDao = mapValueDao;
    }

    @Override
    public void initialize(GenericApplicationContext context) {
        for (int workerId = 0; workerId < numberOfWorkers; workerId++) {
            CommandPollerWorker worker = new CommandPollerWorker(
                    workerId,
                    numberOfWorkers,
                    limit,
                    commandsClient,
                    mapValueDao);
            context.registerBean("commandPollerWorker-" + workerId, CommandPollerWorker.class, () -> worker);
        }
    }
}
