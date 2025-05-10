package com.archiwork.aggregator.poller;

import com.archiwork.aggregator.client.Command;
import com.archiwork.aggregator.client.CommandsClient;
import com.archiwork.aggregator.map.MapValue;
import com.archiwork.aggregator.map.MapValueDao;
import com.archiwork.commons.ReliableTask;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class CommandPoller extends ReliableTask {

    @Value("${polling.limit:1000}")
    private int limit;

    private final CommandsClient commandsClient;
    private final MapValueDao mapValueDao;

    private volatile long toAcknowledge = -1;

    protected CommandPoller(CommandsClient commandsClient, MapValueDao mapValueDao) {
        super("command-poller", false);
        this.commandsClient = commandsClient;
        this.mapValueDao = mapValueDao;
    }

    @Override
    protected void runWithResources() throws Exception {
        while (isRunning()) {
            List<Command> commands = commandsClient.acknowledgeCommands("aggregator", toAcknowledge, limit);
            mapValueDao.insertBatch(commands.stream()
                    .map(c -> new MapValue(c.commandDate(), c.mapId(), c.mapKey(), c.mapValue()))
                    .toList()
            );
            toAcknowledge = commands.stream()
                    .max(Comparator.comparingLong(Command::id))
                    .map(Command::id).orElse(-1L);
        }
    }
}
