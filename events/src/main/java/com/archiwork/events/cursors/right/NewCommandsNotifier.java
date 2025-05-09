package com.archiwork.events.cursors.right;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

@Component
public class NewCommandsNotifier {

    private record Listener(long sinceIndex, Consumer<Long> callback) {
    }

    private final ConcurrentLinkedQueue<Listener> newCommandsListeners = new ConcurrentLinkedQueue<>();
    private final AtomicLong lastIndex = new AtomicLong();

    public void subscribe(long sinceIndex, Consumer<Long> callback) {
        newCommandsListeners.add(new Listener(sinceIndex, callback));
    }

    public void unsubscribe(Consumer<Long> callback) {
        newCommandsListeners.removeIf(listener -> listener.callback.equals(callback));
    }

    @EventListener
    public void handleNewCommand(NewCommandEvent event) {
        final long index = Long.getLong(event.payload());
        long currentValue = lastIndex.get();
        while (currentValue < index) {
            lastIndex.compareAndSet(currentValue, index);
            currentValue = lastIndex.get();
        }

        final Iterator<Listener> it = newCommandsListeners.iterator();
        while (it.hasNext()) {
            Listener listener = it.next();
            if (listener.sinceIndex < lastIndex.get()) {
                listener.callback.accept(lastIndex.get());
                it.remove();
            }
        }
    }
}
