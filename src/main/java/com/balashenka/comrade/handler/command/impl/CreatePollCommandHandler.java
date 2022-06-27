package com.balashenka.comrade.handler.command.impl;

import com.balashenka.comrade.entity.type.AttachmentType;
import com.balashenka.comrade.entity.type.CommandType;
import com.balashenka.comrade.entity.webex.Message;
import com.balashenka.comrade.factory.AttachmentFactoryProvider;
import com.balashenka.comrade.handler.command.CommandHandler;
import com.balashenka.comrade.util.CommandUtil;
import com.balashenka.comrade.util.MessageUtil;
import com.balashenka.comrade.util.locale.AttachmentLocaleText;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;


@Component
public class CreatePollCommandHandler implements CommandHandler {
    private final AttachmentFactoryProvider attachmentFactoryProvider;
    private final MessageUtil messageUtil;

    @Autowired
    public CreatePollCommandHandler(AttachmentFactoryProvider attachmentFactoryProvider,
                                    MessageUtil messageUtil) {
        this.attachmentFactoryProvider = attachmentFactoryProvider;
        this.messageUtil = messageUtil;
    }

    @Override
    public void execute(@NonNull Message message) {
        var text = message.getText().trim().split(CommandUtil.COMMAND_SPLIT_REGEX);

        if (text.length >= 1) {
            attachmentFactoryProvider.getFactory(AttachmentType.POLL_CREATE).createGroup(message.getRoomId(),
                    messageUtil.getText(AttachmentLocaleText.ATTACHMENT_POLL_CREATE_TITLE_TEXT),
                    messageUtil.getText(AttachmentLocaleText.ATTACHMENT_POLL_CREATE_QUESTION_TEXT),
                    messageUtil.getText(AttachmentLocaleText.ATTACHMENT_POLL_CREATE_QUESTION_PLACEHOLDER_TEXT),
                    messageUtil.getText(AttachmentLocaleText.ATTACHMENT_POLL_CREATE_ANSWERS_TEXT),
                    messageUtil.getText(AttachmentLocaleText.ATTACHMENT_POLL_CREATE_ANSWERS_PLACEHOLDER_TEXT),
                    messageUtil.getText(AttachmentLocaleText.ATTACHMENT_POLL_CREATE_BUTTON_SAVE_TEXT));
        }
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.CREATE_POLL;
    }
}