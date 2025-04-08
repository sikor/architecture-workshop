package com.archiwork.events.commands.left;

import com.archiwork.events.commands.right.Command;
import com.archiwork.events.commands.right.CommandDao;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/commands")
public class CommandController {

    private final CommandDao commandDao;

    public CommandController(CommandDao commandDao) {
        this.commandDao = commandDao;
    }

    @PostMapping
    public ResponseEntity<Void> addCommands(@RequestBody List<Command> commands) {
        commandDao.addCommands(commands);
        return ResponseEntity.ok().build();
    }
}
