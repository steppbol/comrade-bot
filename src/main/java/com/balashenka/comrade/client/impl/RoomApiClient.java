package com.balashenka.comrade.client.impl;

import com.balashenka.comrade.client.WebexApiClient;
import com.balashenka.comrade.entity.webex.Room;
import org.springframework.stereotype.Component;

@Component
public record RoomApiClient(WebexApiClient client) {
    private static final String ROOMS_PATH = "/rooms";

    public Room create(Room room) {
        return client.create(room, ROOMS_PATH, Room.class);
    }

    public boolean delete(String id) {
        return client.delete(id, ROOMS_PATH);
    }
}
