package com.archiwork.events.cursors.left;

import com.archiwork.events.cursors.right.CursorDao;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cursors")
public class CursorsController {

    private final CursorDao cursorDao;

    public CursorsController(CursorDao cursorDao) {
        this.cursorDao = cursorDao;
    }

    @GetMapping("/{serviceName}")
    public CursorResponse getCursor(@PathVariable String serviceName) {
        long cursorIndex = cursorDao.getCursorIndex(serviceName).orElse(-1L);
        return new CursorResponse(cursorIndex);
    }

    // Response DTO using Java record
    public record CursorResponse(long cursorIndex) { }
}
