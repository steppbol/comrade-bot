package com.balashenka.comrade.client.impl;

import com.balashenka.comrade.client.WebexApiClient;
import com.balashenka.comrade.entity.webex.Message;
import org.springframework.stereotype.Component;

@Component
public record MessageApiClient(WebexApiClient client) {
    private static final String MESSAGES_PATH = "/messages";

    public Message create(Message message) {
        return client.create(message, MESSAGES_PATH, Message.class);
    }

    public Message get(String id) {
        return client.get(id, MESSAGES_PATH, null, Message.class);
    }

    public boolean delete(String id) {
        return client.delete(id, MESSAGES_PATH);
    }

}
