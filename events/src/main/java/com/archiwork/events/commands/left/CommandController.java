package com.archiwork.events.commands.left;

import com.archiwork.events.commands.right.AddCommand;
import com.archiwork.events.commands.right.CommandDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/commands")
public class CommandController {

    private final CommandDao commandDao;
    private static final Logger log = LoggerFactory.getLogger(CommandController.class);

    public CommandController(CommandDao commandDao) {
        this.commandDao = commandDao;
    }

    @PostMapping
    public ResponseEntity<Long> addCommands(@RequestBody List<AddCommand> commands) {
        List<Number> result = commandDao.addCommands(commands);
        return ResponseEntity.ok(result.getLast().longValue());
    }
}
