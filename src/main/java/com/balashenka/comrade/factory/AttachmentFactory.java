package com.balashenka.comrade.factory;

import com.balashenka.comrade.entity.type.AttachmentType;
import com.balashenka.comrade.entity.webex.Message;

public interface AttachmentFactory {
    Message create(String email, String... arguments);

    Message createGroup(String roomId, String... arguments);

    AttachmentType getAttachmentType();
}
