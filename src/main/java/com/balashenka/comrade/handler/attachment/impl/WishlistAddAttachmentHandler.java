package com.balashenka.comrade.handler.attachment.impl;

import com.balashenka.comrade.entity.type.AttachmentType;
import com.balashenka.comrade.entity.webex.AttachmentAction;
import com.balashenka.comrade.facade.WebexFacade;
import com.balashenka.comrade.model.Person;
import com.balashenka.comrade.util.MessageUtil;
import com.balashenka.comrade.util.locale.CommandLocaleText;
import com.balashenka.comrade.util.locale.ReplyLocaleText;
import com.balashenka.comrade.handler.attachment.AttachmentHandler;
import com.balashenka.comrade.service.PersonService;
import com.balashenka.comrade.service.SpaceService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
public class WishlistAddAttachmentHandler implements AttachmentHandler {
    private static final String ACTION_FIELD = "action";
    private static final String SAVE_ACTION = "SAVE";
    private static final String LINK_TEXT_INPUT = "LINK";
    private static final String EMAIL_INPUT = "EMAIL";
    private static final String GROUP_NAME_INPUT = "GROUP_NAME";

    private final WebexFacade webexFacade;
    private final PersonService personService;
    private final SpaceService spaceService;
    private final MessageUtil messageUtil;

    @Autowired
    public WishlistAddAttachmentHandler(WebexFacade webexFacade,
                                        PersonService personService,
                                        SpaceService spaceService,
                                        MessageUtil messageUtil) {
        this.webexFacade = webexFacade;
        this.personService = personService;
        this.spaceService = spaceService;
        this.messageUtil = messageUtil;
    }

    @Override
    public void handle(@NonNull AttachmentAction attachmentAction) {
        var inputs = attachmentAction.getInputs();
        var action = inputs.get(ACTION_FIELD);

        if (action.equals(SAVE_ACTION)) {
            handleSaveAction(inputs);
        }
    }

    @Override
    public AttachmentType getAttachmentType() {
        return AttachmentType.ADD_WISHLIST;
    }

    private void handleSaveAction(@NonNull Map<String, String> inputs) {
        var wishlist = inputs.get(LINK_TEXT_INPUT);
        var email = inputs.get(EMAIL_INPUT);
        var groupName = inputs.get(GROUP_NAME_INPUT);

        List<Person> persons;
        if (groupName.equals(messageUtil.getText(CommandLocaleText.COMMAND_ARGUMENT_ALL))) {
            persons = personService.findAllByEmail(email);
        } else {
            persons = Collections.singletonList(personService.findByEmailAndGroupName(groupName, groupName));
        }

        if (persons != null && persons.size() > 0) {
            for (var person : persons) {
                person.setWishlist(wishlist);

                var replyDirectText = "";
                if (messageUtil.isUrl(wishlist)) {
                    replyDirectText = messageUtil.getText(ReplyLocaleText.REPLY_DIRECT_WISHLIST_LINK_SAVE, groupName, wishlist);
                } else {
                    replyDirectText = messageUtil.getText(ReplyLocaleText.REPLY_DIRECT_WISHLIST_TEXT_SAVE, groupName, wishlist);
                }

                webexFacade.sendDirectMessage(email, replyDirectText);

                personService.save(person);

                updateMessage(person);
            }
        } else {
            webexFacade.sendDirectMessage(email, messageUtil.getText(ReplyLocaleText.REPLY_DIRECT_GROUP_NOT_FOUND, groupName));
        }
    }

    private void updateMessage(@NonNull Person person) {
        var space = person.getSpace();
        if (space != null) {
            var messageId = space.getMessageId();
            if (messageId != null && !messageId.isBlank()) {
                try {
                    webexFacade.deleteMessage(messageId);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }

                String text;
                var wishlist = person.getWishlist();
                if (messageUtil.isUrl(wishlist)) {
                    text = messageUtil.getText(ReplyLocaleText.REPLY_GROUP_WISHLIST_LINK_TEXT, person.getName(), person.getEmail(), person.getWishlist());
                } else {
                    if (wishlist != null && !wishlist.isBlank()) {
                        text = messageUtil.getText(ReplyLocaleText.REPLY_GROUP_WISHLIST_TEXT_TEXT, person.getName(), person.getEmail(), person.getWishlist());
                    } else {
                        text = messageUtil.getText(ReplyLocaleText.REPLY_GROUP_WISHLIST_EMPTY_TEXT, person.getName(), person.getEmail());
                    }
                }

                text = messageUtil.getText(ReplyLocaleText.REPLY_GROUP_WISHLIST_SAVE) + MessageUtil.NEW_LINE_HTML_SYMBOL + text;

                var created = webexFacade.sendMessage(person.getSpace().getRoomId(), text);

                space.setMessageId(created.getId());
                spaceService.save(space);
            }
        }
    }
}
