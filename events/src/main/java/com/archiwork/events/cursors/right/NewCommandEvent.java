package com.archiwork.events.cursors.right;

public class NewCommandEvent {
    private final String payload;

    public NewCommandEvent(String payload) {
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }
}
