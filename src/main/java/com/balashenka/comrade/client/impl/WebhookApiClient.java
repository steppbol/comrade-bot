package com.balashenka.comrade.client.impl;

import com.balashenka.comrade.client.WebexApiClient;
import com.balashenka.comrade.entity.webex.Webhook;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public record WebhookApiClient(WebexApiClient client) {
    private static final String WEBHOOKS_PATH = "/webhooks";

    public Webhook create(Webhook webhook) {
        return client.create(webhook, WEBHOOKS_PATH, Webhook.class);
    }

    public List<Webhook> getAll() {
        return client.getAll(WEBHOOKS_PATH, null, Webhook.class);
    }

    public boolean delete(String id) {
        return client.delete(id, WEBHOOKS_PATH);
    }
}
