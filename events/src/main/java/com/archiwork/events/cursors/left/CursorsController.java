package com.archiwork.events.cursors.left;

import com.archiwork.events.commands.right.CommandDao;
import com.archiwork.events.commands.right.GetCommand;
import com.archiwork.events.cursors.right.CursorDao;
import com.archiwork.events.cursors.right.NewCommandsNotifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@RestController
@RequestMapping("/cursors")
public class CursorsController {

    private final CursorDao cursorDao;
    private final CommandDao commandDao;
    private final NewCommandsNotifier newCommandsNotifier;

    public CursorsController(CursorDao cursorDao, CommandDao commandDao, NewCommandsNotifier newCommandsNotifier) {
        this.cursorDao = cursorDao;
        this.commandDao = commandDao;
        this.newCommandsNotifier = newCommandsNotifier;
    }

    @PostMapping
    public DeferredResult<List<GetCommand>> acknowledgeProcessedCommands(
            @RequestParam("serviceName") String serviceName,
            @RequestParam(value = "sinceId") Optional<Long> sinceId,
            @RequestParam("limit") int limit) {

        updateCursor(serviceName, sinceId);
        DeferredResult<List<GetCommand>> deferredResult = new DeferredResult<>(
                10_000L, Collections.emptyList());
        handleResult(serviceName, sinceId, limit, deferredResult);

        return deferredResult;
    }

    private void handleResult(
            String serviceName,
            Optional<Long> sinceId,
            int limit,
            DeferredResult<List<GetCommand>> deferredResult) {
        if (limit > 0) {
            long sid = sinceId.orElse(cursorDao.getCursorIndex(serviceName).orElse(-1L));
            List<GetCommand> results = getCommandsSince(limit, sid);
            if (results.isEmpty()) {
                waitForCommands(limit, deferredResult, sid);
            } else {
                deferredResult.setResult(results);
            }
        } else {
            deferredResult.setResult(List.of());
        }
    }

    private void waitForCommands(int limit, DeferredResult<List<GetCommand>> deferredResult, long sid) {
        final Consumer<Long> longConsumer = l -> deferredResult.setResult(getCommandsSince(limit, sid));
        newCommandsNotifier.subscribe(sid, longConsumer);
        // If client cancels request, clean up
        deferredResult.onCompletion(() -> newCommandsNotifier.unsubscribe(longConsumer));
    }

    private void updateCursor(String serviceName, Optional<Long> sinceId) {
        sinceId
                .filter(s -> s >= 0)
                .ifPresent(s -> cursorDao.setCursorIndex(serviceName, s));
    }

    private List<GetCommand> getCommandsSince(int limit, long sid) {
        return commandDao.findByIdGreaterThanOrderByIdAsc(sid, limit);
    }

}
