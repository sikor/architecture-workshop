package com.archiwork.aggregator.poller;

import com.archiwork.aggregator.client.Command;
import com.archiwork.aggregator.client.CommandsClient;
import com.archiwork.aggregator.map.MapValue;
import com.archiwork.aggregator.map.MapValueDao;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Comparator;
import java.util.List;

public class CommandPollerWorker {

    private final int workerId;
    private final int totalWorkers;
    private final int limit;
    private final CommandsClient commandsClient;
    private final MapValueDao mapValueDao;

    private long toAcknowledge = -1;

    public CommandPollerWorker(int workerId,
                               int totalWorkers,
                               int limit,
                               CommandsClient commandsClient,
                               MapValueDao mapValueDao) {
        this.workerId = workerId;
        this.totalWorkers = totalWorkers;
        this.limit = limit;
        this.commandsClient = commandsClient;
        this.mapValueDao = mapValueDao;
    }

    @Scheduled(fixedRateString = "${polling.interval.millis:1000}")
    public void pollCommands() {
        List<Command> commands = commandsClient.acknowledgeCommands(toAcknowledge, limit);
        mapValueDao.insertBatch(commands.stream()
                .map(c -> new MapValue(c.commandDate(), c.mapId(), c.mapKey(), c.mapValue()))
                .toList()
        );
        toAcknowledge = commands.stream()
                .max(Comparator.comparingLong(Command::id))
                .map(Command::id).orElse(-1L);
    }
}
