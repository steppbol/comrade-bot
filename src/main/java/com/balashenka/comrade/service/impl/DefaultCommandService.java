package com.balashenka.comrade.service.impl;

import com.balashenka.comrade.entity.type.CommandType;
import com.balashenka.comrade.entity.webex.Message;
import com.balashenka.comrade.facade.WebexFacade;
import com.balashenka.comrade.handler.command.CommandHandler;
import com.balashenka.comrade.service.CommandService;
import com.balashenka.comrade.util.CommandUtil;
import com.balashenka.comrade.util.MessageUtil;
import com.balashenka.comrade.util.locale.CommandLocaleText;
import com.balashenka.comrade.configuration.ComradeProperty;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class DefaultCommandService implements CommandService {
    private final WebexFacade webexFacade;
    private final CommandUtil commandUtil;
    private final ComradeProperty property;

    private final Map<String, CommandType> commandTypes = new HashMap<>();
    private final Map<CommandType, CommandHandler> commands = new HashMap<>();

    @Autowired
    public DefaultCommandService(WebexFacade webexFacade,
                                 ComradeProperty property,
                                 @NonNull List<CommandHandler> commands,
                                 @NonNull MessageUtil messageUtil,
                                 CommandUtil commandUtil) {
        this.webexFacade = webexFacade;
        this.property = property;
        this.commandUtil = commandUtil;

        commandTypes.put(messageUtil.getText(CommandLocaleText.COMMAND_HELP), CommandType.HELP);
        commandTypes.put(messageUtil.getText(CommandLocaleText.COMMAND_SHOW_WISHLIST), CommandType.SHOW_WISHLIST);
        commandTypes.put(messageUtil.getText(CommandLocaleText.COMMAND_ADD_WISHLIST), CommandType.ADD_WISHLIST);
        commandTypes.put(messageUtil.getText(CommandLocaleText.COMMAND_DELETE_WISHLIST), CommandType.DELETE_WISHLIST);
        commandTypes.put(messageUtil.getText(CommandLocaleText.COMMAND_IGNORE_DATE), CommandType.IGNORE_DATE);
        commandTypes.put(messageUtil.getText(CommandLocaleText.COMMAND_NOTIFY_DATE), CommandType.NOTIFY_DATE);
        commandTypes.put(messageUtil.getText(CommandLocaleText.COMMAND_SHOW_GROUP), CommandType.SHOW_GROUP);
        commandTypes.put(messageUtil.getText(CommandLocaleText.COMMAND_CREATE_POLL), CommandType.CREATE_POLL);

        commands.forEach(command -> this.commands.put(command.getCommandType(), command));
    }

    @Override
    public void handle(String personEmail, String messageId) {
        if (personEmail != null && !personEmail.equals(property.getBot().getEmail())) {
            var receivedMessage = webexFacade.getMessage(messageId);

            String[] text;
            if (receivedMessage.getMentionedPeople() != null &&
                    receivedMessage.getMentionedPeople().contains(property.getBot().getId()) && receivedMessage.getRoomType().equals(CommandUtil.GROUP_ROOM_TYPE)) {
                text = receivedMessage.getText().replace(property.getBot().getNickname(), "").trim().split(CommandUtil.COMMAND_SPLIT_REGEX);
                webexFacade.deleteMessage(messageId);
            } else {
                text = receivedMessage.getText().trim().split(CommandUtil.COMMAND_SPLIT_REGEX);
            }

            execute(receivedMessage, text);
        }
    }

    private void execute(Message receivedMessage, String[] text) {
        var command = commands.get(commandTypes.get(commandUtil.fetchArgument(text, 0)));

        if (command != null) {
            log.info("Execute command: {}", command.getCommandType());
            command.execute(receivedMessage);
        }
    }
}
