package com.balashenka.comrade.handler.attachment.impl;

import com.balashenka.comrade.entity.type.AttachmentType;
import com.balashenka.comrade.entity.webex.AttachmentAction;
import com.balashenka.comrade.facade.WebexFacade;
import com.balashenka.comrade.util.AttachmentUtil;
import com.balashenka.comrade.util.MessageUtil;
import com.balashenka.comrade.util.locale.ReplyLocaleText;
import com.balashenka.comrade.handler.attachment.AttachmentHandler;
import com.balashenka.comrade.service.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class PollAttachmentHandler implements AttachmentHandler {
    private static final String CHOICE_SPLIT_SYMBOL = ",";
    private static final String ACTION_FIELD = "action";
    private static final String SAVE_ACTION = "SAVE";
    private static final String RESULT_ACTION = "RESULT";
    private static final String FINISH_ACTION = "FINISH";
    private static final String CHOICE_SET = "CHOICE_SET";

    private final AttachmentUtil attachmentUtil;
    private final PollService pollService;
    private final MessageUtil messageUtil;
    private final WebexFacade webexFacade;

    @Autowired
    public PollAttachmentHandler(AttachmentUtil attachmentUtil, PollService pollService, MessageUtil messageUtil, WebexFacade webexFacade) {
        this.attachmentUtil = attachmentUtil;
        this.pollService = pollService;
        this.messageUtil = messageUtil;
        this.webexFacade = webexFacade;
    }

    @Override
    public void handle(AttachmentAction attachmentAction) {
        var inputs = attachmentAction.getInputs();
        var action = inputs.get(ACTION_FIELD);

        switch (action) {
            case SAVE_ACTION -> handleSave(attachmentAction);
            case RESULT_ACTION -> handleResult(attachmentAction);
            case FINISH_ACTION -> handleFinish(attachmentAction);
        }
    }

    @Override
    public AttachmentType getAttachmentType() {
        return AttachmentType.POLL;
    }

    private void handleSave(@NonNull AttachmentAction attachmentAction) {
        var messageId = attachmentAction.getMessageId();
        var choices = Arrays.stream(attachmentAction.getInputs().get(CHOICE_SET).split(CHOICE_SPLIT_SYMBOL)).toList();
        pollService.updateChoices(messageId, choices);
    }

    private void handleResult(@NonNull AttachmentAction attachmentAction) {
        var messageId = attachmentAction.getMessageId();
        var choices = pollService.getResult(messageId);
        var result = new StringBuilder();

        result.append(messageUtil.getText(ReplyLocaleText.REPLY_POLL_RESULT, pollService.getByMessageId(messageId).getTitle())).append(MessageUtil.NEW_LINE_HTML_SYMBOL);
        for (var choice : choices.entrySet()) {
            result.append(messageUtil.getText(ReplyLocaleText.REPLY_POLL_RESULT_ROW, choice.getKey(), choice.getValue().toString())).append(MessageUtil.NEW_LINE_HTML_SYMBOL);
        }

        webexFacade.sendMessage(attachmentAction.getRoomId(), result.toString());
    }

    private void handleFinish(@NonNull AttachmentAction attachmentAction) {
        var messageId = attachmentAction.getMessageId();

        webexFacade.sendMessage(attachmentAction.getRoomId(),
                messageUtil.getText(ReplyLocaleText.REPLY_POLL_FINISH, pollService.getByMessageId(messageId).getTitle())
                        + MessageUtil.NEW_LINE_HTML_SYMBOL + attachmentUtil.buildFinish(pollService.getResult(messageId)));

        pollService.deleteByMessageId(messageId);
    }
}
