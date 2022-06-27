package com.balashenka.comrade.handler.attachment.impl;

import com.balashenka.comrade.entity.type.AttachmentType;
import com.balashenka.comrade.entity.webex.AttachmentAction;
import com.balashenka.comrade.facade.WebexFacade;
import com.balashenka.comrade.model.Person;
import com.balashenka.comrade.util.MessageUtil;
import com.balashenka.comrade.util.locale.ReplyLocaleText;
import com.balashenka.comrade.handler.attachment.AttachmentHandler;
import com.balashenka.comrade.service.PersonService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Map;

@Log4j2
@Component
public class WishlistUpdateAttachmentHandler implements AttachmentHandler {
    private static final String ACTION_FIELD = "action";
    private static final String SAVE_ACTION = "SAVE";
    private static final String DELETE_ACTION = "DELETE";
    private static final String LINK_TEXT_INPUT = "LINK";
    private static final String EMAIL_INPUT = "EMAIL";
    private static final String GROUP_NAME_INPUT = "GROUP_NAME";

    private final WebexFacade webexFacade;
    private final PersonService personService;
    private final MessageUtil messageUtil;

    @Autowired
    public WishlistUpdateAttachmentHandler(WebexFacade webexFacade,
                                           PersonService personService,
                                           MessageUtil messageUtil) {
        this.webexFacade = webexFacade;
        this.personService = personService;
        this.messageUtil = messageUtil;
    }

    @Override
    public void handle(@NonNull AttachmentAction attachmentAction) {
        var inputs = attachmentAction.getInputs();
        var action = inputs.get(ACTION_FIELD);

        var email = inputs.get(EMAIL_INPUT);
        var groupName = inputs.get(GROUP_NAME_INPUT);
        var person = personService.findByEmailAndGroupName(email, groupName);

        Response response = null;
        if (person != null) {
            if (action.equals(SAVE_ACTION)) {
                response = handleSaveAction(person, inputs);
            } else if (action.equals(DELETE_ACTION)) {
                response = handleDeleteAction(person, inputs);
            }

            if (response != null) {
                webexFacade.sendDirectMessage(email, response.personMessage());
                updateAttachment(person, response);
            }

            personService.save(person);
        } else {
            webexFacade.sendDirectMessage(email, messageUtil.getText(ReplyLocaleText.REPLY_DIRECT_GROUP_NOT_FOUND, groupName));
        }
    }

    @Override
    public AttachmentType getAttachmentType() {
        return AttachmentType.UPDATE_WISHLIST;
    }

    @NonNull
    private Response handleSaveAction(@NonNull Person person, @NonNull Map<String, String> inputs) {
        var wishlist = inputs.get(LINK_TEXT_INPUT);
        var groupName = inputs.get(GROUP_NAME_INPUT);

        person.setWishlist(wishlist);

        String personMessage;
        if (messageUtil.isUrl(wishlist)) {
            personMessage = messageUtil.getText(ReplyLocaleText.REPLY_DIRECT_WISHLIST_LINK_SAVE, groupName, wishlist);
        } else {
            personMessage = messageUtil.getText(ReplyLocaleText.REPLY_DIRECT_WISHLIST_TEXT_SAVE, groupName, wishlist);
        }

        String groupMessage;
        if (!messageUtil.isUrl(wishlist)) {
            if (wishlist != null && !wishlist.isBlank()) {
                groupMessage = messageUtil.getText(ReplyLocaleText.REPLY_GROUP_WISHLIST_TEXT_TEXT, person.getName(), person.getEmail(), person.getWishlist());
            } else {
                groupMessage = messageUtil.getText(ReplyLocaleText.REPLY_GROUP_WISHLIST_EMPTY_TEXT, person.getName(), person.getEmail());
            }
        } else {
            groupMessage = messageUtil.getText(ReplyLocaleText.REPLY_GROUP_WISHLIST_LINK_TEXT, person.getName(), person.getEmail(), person.getWishlist());

        }

        return new Response(messageUtil.getText(ReplyLocaleText.REPLY_GROUP_WISHLIST_SAVE) + MessageUtil.NEW_LINE_HTML_SYMBOL + groupMessage,
                personMessage);
    }

    @NonNull
    private Response handleDeleteAction(@NonNull Person person, @NonNull Map<String, String> inputs) {
        var groupName = inputs.get(GROUP_NAME_INPUT);

        person.setWishlist("");

        return new Response(messageUtil.getText(ReplyLocaleText.REPLY_GROUP_WISHLIST_DELETE), messageUtil.getText(ReplyLocaleText.REPLY_DIRECT_WISHLIST_DELETE, groupName));
    }

    private void updateAttachment(@NonNull Person person, @NonNull Response response) {
        var space = person.getSpace();
        if (space != null) {
            var messageId = space.getMessageId();
            if (messageId != null && !messageId.isBlank()) {
                try {
                    webexFacade.deleteMessage(messageId);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }

                webexFacade.sendMessage(space.getRoomId(), response.groupMessage());
            }
        }
    }

    private record Response(String groupMessage, String personMessage) {
    }
}
