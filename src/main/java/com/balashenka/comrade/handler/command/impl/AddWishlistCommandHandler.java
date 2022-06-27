package com.balashenka.comrade.handler.command.impl;

import com.balashenka.comrade.entity.type.AttachmentType;
import com.balashenka.comrade.entity.type.CommandType;
import com.balashenka.comrade.entity.webex.Message;
import com.balashenka.comrade.facade.WebexFacade;
import com.balashenka.comrade.factory.AttachmentFactoryProvider;
import com.balashenka.comrade.util.CommandUtil;
import com.balashenka.comrade.util.MessageUtil;
import com.balashenka.comrade.util.locale.AttachmentLocaleText;
import com.balashenka.comrade.util.locale.CommandLocaleText;
import com.balashenka.comrade.util.locale.ReplyLocaleText;
import com.balashenka.comrade.handler.command.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class AddWishlistCommandHandler implements CommandHandler {
    private final WebexFacade webexFacade;
    private final AttachmentFactoryProvider attachmentFactoryProvider;
    private final CommandUtil commandUtil;
    private final MessageUtil messageUtil;

    @Autowired
    public AddWishlistCommandHandler(WebexFacade webexFacade,
                                     AttachmentFactoryProvider attachmentFactoryProvider,
                                     CommandUtil commandUtil,
                                     MessageUtil messageUtil) {
        this.webexFacade = webexFacade;
        this.attachmentFactoryProvider = attachmentFactoryProvider;
        this.commandUtil = commandUtil;
        this.messageUtil = messageUtil;
    }

    @Override
    public void execute(@NonNull Message message) {
        var text = message.getText().trim().split(CommandUtil.COMMAND_SPLIT_REGEX);

        if (text.length >= 2) {
            var groupName = commandUtil.fetchArgument(text, 1);
            var persons = commandUtil.getPersons(groupName, message.getPersonEmail());
            if (groupName.equals(CommandLocaleText.COMMAND_ARGUMENT_ALL)) {
                attachmentFactoryProvider.getFactory(AttachmentType.ADD_WISHLIST)
                        .create(message.getPersonEmail(),
                                message.getPersonEmail(),
                                groupName,
                                messageUtil.getText(AttachmentLocaleText.ATTACHMENT_WISHLIST_ADD_MESSAGE_TEXT, groupName),
                                messageUtil.getText(AttachmentLocaleText.ATTACHMENT_WISHLIST_ADD_INPUT_TEXT),
                                messageUtil.getText(AttachmentLocaleText.ATTACHMENT_WISHLIST_ADD_BUTTON_SAVE_TEXT));
            } else {
                if (persons.size() > 0) {
                    for (var person : persons) {
                        attachmentFactoryProvider.getFactory(AttachmentType.ADD_WISHLIST)
                                .create(person.getEmail(),
                                        person.getEmail(),
                                        groupName,
                                        messageUtil.getText(AttachmentLocaleText.ATTACHMENT_WISHLIST_ADD_MESSAGE_TEXT, groupName),
                                        messageUtil.getText(AttachmentLocaleText.ATTACHMENT_WISHLIST_ADD_INPUT_TEXT),
                                        messageUtil.getText(AttachmentLocaleText.ATTACHMENT_WISHLIST_ADD_BUTTON_SAVE_TEXT));
                    }
                } else {
                    webexFacade.sendMessage(message.getRoomId(), messageUtil.getText(ReplyLocaleText.REPLY_DIRECT_GROUP_NOT_FOUND, groupName));
                }
            }
        } else {
            webexFacade.sendDirectMessage(message.getPersonEmail(), messageUtil.getText(ReplyLocaleText.REPLY_COMMAND_ADD_WISHLIST_BAD_FORMAT,
                    messageUtil.getText(CommandLocaleText.COMMAND_ADD_WISHLIST), messageUtil.getText(CommandLocaleText.COMMAND_ARGUMENT_ALL)));
        }
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.ADD_WISHLIST;
    }
}
