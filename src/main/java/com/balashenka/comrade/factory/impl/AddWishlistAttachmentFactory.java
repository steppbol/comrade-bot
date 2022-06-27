package com.balashenka.comrade.factory.impl;

import com.balashenka.comrade.entity.type.AttachmentType;
import com.balashenka.comrade.entity.webex.Message;
import com.balashenka.comrade.facade.WebexFacade;
import com.balashenka.comrade.factory.AttachmentFactory;
import com.balashenka.comrade.util.MessageUtil;
import com.balashenka.comrade.util.locale.AttachmentLocaleText;
import com.balashenka.comrade.configuration.ComradeProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;


@Component
public class AddWishlistAttachmentFactory implements AttachmentFactory {
    private final String addWishlistPath;

    private final WebexFacade webexFacade;
    private final MessageUtil messageUtil;

    @Autowired
    public AddWishlistAttachmentFactory(WebexFacade webexFacade, MessageUtil messageUtil, @NonNull ComradeProperty property) {
        this.webexFacade = webexFacade;
        this.messageUtil = messageUtil;

        addWishlistPath = property.getAttachment().getPath() + "/add_wishlist.json";
    }

    @Override
    public Message create(String email, String... arguments) {
        return create((x, y) -> webexFacade.sendDirectMessage(email, x, y), arguments);
    }

    @Override
    public Message createGroup(String roomId, String... arguments) {
        return create((x, y) -> webexFacade.sendMessage(roomId, x, y), arguments);
    }

    private Message create(@NonNull BiFunction<String, List<Map<String, ?>>, Message> function, String... arguments) {
        return function.apply(messageUtil.getText(AttachmentLocaleText.ATTACHMENT_WISHLIST_ADD_TEXT), messageUtil.getAttachment(addWishlistPath, arguments));
    }

    @Override
    public AttachmentType getAttachmentType() {
        return AttachmentType.ADD_WISHLIST;
    }
}
