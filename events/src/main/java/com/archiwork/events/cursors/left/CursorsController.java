package com.archiwork.events.cursors.left;

import com.archiwork.events.commands.right.CommandDao;
import com.archiwork.events.commands.right.GetCommand;
import com.archiwork.events.cursors.right.CursorDao;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cursors")
public class CursorsController {

    private final CursorDao cursorDao;
    private final CommandDao commandDao;

    public CursorsController(CursorDao cursorDao, CommandDao commandDao) {
        this.cursorDao = cursorDao;
        this.commandDao = commandDao;
    }

    @PostMapping
    public DeferredResult<List<GetCommand>> acknowledgeProcessedCommands(
            @RequestParam("serviceName") String serviceName,
            @RequestParam(value = "sinceId") Optional<Long> sinceId,
            @RequestParam("limit") int limit) {
        DeferredResult<List<GetCommand>> deferredResult = new DeferredResult<>(
                10_000L, Collections.emptyList());

        sinceId
                .filter(s -> s >= 0)
                .ifPresent(s -> cursorDao.setCursorIndex(serviceName, s));
        if (limit > 0) {
            long sid = sinceId.orElse(cursorDao.getCursorIndex(serviceName).orElse(-1L));
            List<GetCommand> results = commandDao.findByIdGreaterThanOrderByIdAsc(sid, limit);
            if (results.isEmpty()) {
                commandDao.registerCallback(deferredResult::setResult);
                // If client cancels request, clean up
                deferredResult.onCompletion(commandDao::unregisterCallback);
            } else {
                deferredResult.setResult(results);
            }
        } else {
            deferredResult.setResult(List.of());
        }

        return deferredResult;
    }

}
