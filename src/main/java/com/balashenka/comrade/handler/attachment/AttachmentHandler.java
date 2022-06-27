package com.balashenka.comrade.handler.attachment;

import com.balashenka.comrade.entity.type.AttachmentType;
import com.balashenka.comrade.entity.webex.AttachmentAction;

public interface AttachmentHandler {
    void handle(AttachmentAction attachmentAction);

    AttachmentType getAttachmentType();
}
