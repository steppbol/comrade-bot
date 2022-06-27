package com.balashenka.comrade.handler.attachment.impl;

import com.balashenka.comrade.entity.type.AttachmentType;
import com.balashenka.comrade.entity.webex.AttachmentAction;
import com.balashenka.comrade.facade.WebexFacade;
import com.balashenka.comrade.factory.AttachmentFactoryProvider;
import com.balashenka.comrade.model.Choice;
import com.balashenka.comrade.util.MessageUtil;
import com.balashenka.comrade.util.locale.AttachmentLocaleText;
import com.balashenka.comrade.util.locale.ReplyLocaleText;
import com.balashenka.comrade.handler.attachment.AttachmentHandler;
import com.balashenka.comrade.service.PollService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Component
public class PollCreateAttachmentHandler implements AttachmentHandler {
    public static final String ANSWER_SPLIT_REGEX = ";";

    private static final String ACTION_FIELD = "action";
    private static final String SAVE_ACTION = "SAVE";
    private static final String ANSWER_INPUT = "ANSWER";
    private static final String QUESTION_INPUT = "QUESTION";

    private final WebexFacade webexFacade;
    private final MessageUtil messageUtil;
    private final PollService pollService;
    private final AttachmentFactoryProvider attachmentFactoryProvider;

    @Autowired
    public PollCreateAttachmentHandler(WebexFacade webexFacade,
                                       MessageUtil messageUtil, PollService pollService, AttachmentFactoryProvider attachmentFactoryProvider) {
        this.webexFacade = webexFacade;
        this.messageUtil = messageUtil;
        this.pollService = pollService;
        this.attachmentFactoryProvider = attachmentFactoryProvider;
    }

    @Override
    public void handle(@NonNull AttachmentAction attachmentAction) {
        var inputs = attachmentAction.getInputs();
        var action = inputs.get(ACTION_FIELD);

        if (action.equals(SAVE_ACTION)) {
            handleSaveAction(attachmentAction, inputs);
        }
    }

    @Override
    public AttachmentType getAttachmentType() {
        return AttachmentType.POLL_CREATE;
    }

    private void handleSaveAction(AttachmentAction attachmentAction, @NonNull Map<String, String> inputs) {
        var answer = inputs.get(ANSWER_INPUT);
        var question = inputs.get(QUESTION_INPUT);

        if (answer != null && !answer.isBlank() && question != null && !question.isBlank()) {
            var answers = answer.trim().split(ANSWER_SPLIT_REGEX);
            if (answers.length >= 2) {
                createPoll(attachmentAction.getRoomId(), question, Arrays.stream(answers).toList());
            } else {
                webexFacade.sendMessage(attachmentAction.getRoomId(), messageUtil.getText(ReplyLocaleText.REPLY_ENOUGH_ANSWERS_TEXT));
            }
        } else {
            webexFacade.sendMessage(attachmentAction.getRoomId(), messageUtil.getText(ReplyLocaleText.REPLY_EMPTY_FIELD_TEXT));
        }
    }

    private void createPoll(String roomId, String question, @NonNull List<String> answers) {
        var choiceSet = new StringBuilder();

        for (var answer : answers) {
            choiceSet.append(messageUtil.fillTextContent("""
                    {
                       "title": "${0}",
                       "value": "${1}"
                    }
                    """, answer, answer));
            choiceSet.append(",");
        }

        choiceSet.deleteCharAt(choiceSet.length() - 1);

        var message = attachmentFactoryProvider.getFactory(AttachmentType.POLL)
                .createGroup(roomId, question,
                        choiceSet.toString(),
                        messageUtil.getText(AttachmentLocaleText.ATTACHMENT_POLL_PLACEHOLDER_TEXT),
                        messageUtil.getText(AttachmentLocaleText.ATTACHMENT_POLL_BUTTON_SAVE_TEXT),
                        messageUtil.getText(AttachmentLocaleText.ATTACHMENT_POLL_BUTTON_RESULT_TEXT),
                        messageUtil.getText(AttachmentLocaleText.ATTACHMENT_POLL_BUTTON_FINISH_TEXT));

        pollService.save(message.getId(), roomId, question, answers.stream()
                .map(e -> Choice.builder().choiceText(e).amount(0L).build())
                .collect(Collectors.toSet()));
    }
}