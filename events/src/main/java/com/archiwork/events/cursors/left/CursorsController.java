package com.archiwork.events.cursors.left;

import com.archiwork.events.commands.right.CommandDao;
import com.archiwork.events.commands.right.GetCommand;
import com.archiwork.events.cursors.right.CursorDao;
import org.springframework.web.bind.annotation.*;

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
    public List<GetCommand> acknowledgeProcessedCommands(
            @RequestParam("serviceName") String serviceName,
            @RequestParam(value = "sinceId") Optional<Long> sinceId,
            @RequestParam("limit") int limit) {
        sinceId
                .filter(s -> s >= 0)
                .ifPresent(s -> cursorDao.setCursorIndex(serviceName, s));
        if (limit > 0) {
            long sid = sinceId.orElse(cursorDao.getCursorIndex(serviceName).orElse(-1L));
            return commandDao.findByIdGreaterThanOrderByIdAsc(sid, limit);
        } else {
            return List.of();
        }
    }

}
