package com.balashenka.comrade.handler.command.impl;

import com.balashenka.comrade.entity.type.CommandType;
import com.balashenka.comrade.entity.webex.Message;
import com.balashenka.comrade.facade.WebexFacade;
import com.balashenka.comrade.handler.command.CommandHandler;
import com.balashenka.comrade.util.MessageUtil;
import com.balashenka.comrade.util.locale.ReplyLocaleText;
import com.balashenka.comrade.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ShowGroupCommandHandler implements CommandHandler {
    private final WebexFacade webexFacade;
    private final GroupService groupService;
    private final MessageUtil messageUtil;

    @Autowired
    public ShowGroupCommandHandler(WebexFacade webexFacade, GroupService groupService, MessageUtil messageUtil) {
        this.webexFacade = webexFacade;
        this.groupService = groupService;
        this.messageUtil = messageUtil;
    }

    @Override
    public void execute(@NonNull Message message) {
        var email = message.getPersonEmail();
        var groups = groupService.findAllByPersonEmail(email);

        if (groups.size() > 0) {
            var result = new StringBuilder();

            for (var group : groups) {
                var person = group.getPersons().stream().filter(e -> e.getEmail().equals(email)).findFirst().orElse(null);
                var ignored = false;

                if (person != null) {
                    ignored = person.isIgnoring();
                }

                var reply = messageUtil.getText(ReplyLocaleText.REPLY_COMMAND_SHOW_GROUP, group.getName());

                if (ignored) {
                    reply = reply + " (" + messageUtil.getText(ReplyLocaleText.REPLY_IGNORED) + ")";
                }

                result.append(reply).append(MessageUtil.NEW_LINE_HTML_SYMBOL);
            }
            webexFacade.sendDirectMessage(message.getPersonEmail(), result.toString());
        } else {
            webexFacade.sendDirectMessage(message.getPersonEmail(), messageUtil.getText(ReplyLocaleText.REPLY_DIRECT_ANY_GROUP_NOT_FOUND));
        }
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.SHOW_GROUP;
    }
}
