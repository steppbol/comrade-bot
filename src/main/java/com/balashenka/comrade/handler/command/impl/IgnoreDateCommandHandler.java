package com.balashenka.comrade.handler.command.impl;

import com.balashenka.comrade.entity.type.CommandType;
import com.balashenka.comrade.entity.webex.Message;
import com.balashenka.comrade.facade.WebexFacade;
import com.balashenka.comrade.handler.command.CommandHandler;
import com.balashenka.comrade.util.CommandUtil;
import com.balashenka.comrade.util.MessageUtil;
import com.balashenka.comrade.util.locale.CommandLocaleText;
import com.balashenka.comrade.util.locale.ReplyLocaleText;
import com.balashenka.comrade.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class IgnoreDateCommandHandler implements CommandHandler {
    private final WebexFacade webexFacade;
    private final PersonService personService;
    private final CommandUtil commandUtil;
    private final MessageUtil messageUtil;

    @Autowired
    public IgnoreDateCommandHandler(WebexFacade webexFacade,
                                    PersonService personService,
                                    CommandUtil commandUtil,
                                    MessageUtil messageUtil) {
        this.webexFacade = webexFacade;
        this.personService = personService;
        this.commandUtil = commandUtil;
        this.messageUtil = messageUtil;
    }

    @Override
    public void execute(@NonNull Message message) {
        var text = message.getText().trim().split(CommandUtil.COMMAND_SPLIT_REGEX);

        if (text.length >= 2) {
            var groupName = commandUtil.fetchArgument(text, 1);

            var persons = commandUtil.getPersons(groupName, message.getPersonEmail());
            if (persons.size() > 0) {
                for (var person : persons) {
                    person.setIgnoring(true);
                    personService.save(person);
                }

                webexFacade.sendDirectMessage(message.getPersonEmail(), messageUtil.getText(ReplyLocaleText.REPLY_COMMAND_IGNORE_DATE, groupName));
            } else {
                webexFacade.sendDirectMessage(message.getPersonEmail(), messageUtil.getText(ReplyLocaleText.REPLY_DIRECT_GROUP_NOT_FOUND, groupName));
            }
        } else {
            webexFacade.sendDirectMessage(message.getPersonEmail(), messageUtil.getText(ReplyLocaleText.REPLY_COMMAND_IGNORE_DATE_BAD_FORMAT,
                    messageUtil.getText(CommandLocaleText.COMMAND_IGNORE_DATE), messageUtil.getText(CommandLocaleText.COMMAND_ARGUMENT_ALL)));
        }
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.IGNORE_DATE;
    }
}
