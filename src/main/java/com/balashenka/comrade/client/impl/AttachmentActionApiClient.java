package com.balashenka.comrade.client.impl;

import com.balashenka.comrade.client.WebexApiClient;
import com.balashenka.comrade.entity.webex.AttachmentAction;
import org.springframework.stereotype.Component;

@Component
public record AttachmentActionApiClient(WebexApiClient client) {
    private static final String ATTACHMENT_ACTIONS_PATH = "/attachment/actions";

    public AttachmentAction get(String id) {
        return client.get(id, ATTACHMENT_ACTIONS_PATH, null, AttachmentAction.class);
    }

}
