package com.archiwork.events.cursors.core;

import com.archiwork.events.cursors.right.NewCommandEvent;
import com.archiwork.events.cursors.right.PostgresPubSub;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class CursorsService {

    private final PostgresPubSub postgresPubSub;
    private volatile long lastIndex = -1;

    public CursorsService(PostgresPubSub postgresPubSub) {
        this.postgresPubSub = postgresPubSub;
    }

    public void newCommand(long index) {
        postgresPubSub.publish(String.valueOf(index));
    }

    @EventListener
    public void handleNewCommand(NewCommandEvent event) {
        long index = Long.getLong(event.payload());
    }


}
