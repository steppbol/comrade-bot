package com.balashenka.comrade.handler.command.impl;

import com.balashenka.comrade.entity.type.CommandType;
import com.balashenka.comrade.entity.webex.Message;
import com.balashenka.comrade.facade.WebexFacade;
import com.balashenka.comrade.util.MessageUtil;
import com.balashenka.comrade.util.locale.CommandLocaleText;
import com.balashenka.comrade.util.locale.ReplyLocaleText;
import com.balashenka.comrade.handler.command.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class HelpCommandHandler implements CommandHandler {
    private final WebexFacade webexFacade;
    private final MessageUtil messageUtil;

    @Autowired
    public HelpCommandHandler(WebexFacade webexFacade, MessageUtil messageUtil) {
        this.webexFacade = webexFacade;
        this.messageUtil = messageUtil;
    }

    @Override
    public void execute(@NonNull Message message) {
        var reply = messageUtil.getText(ReplyLocaleText.REPLY_COMMAND_HELP) + MessageUtil.NEW_LINE_HTML_SYMBOL + MessageUtil.NEW_LINE_HTML_SYMBOL +
                messageUtil.getText(ReplyLocaleText.REPLY_COMMAND_HELP_COMMAND_LIST) + MessageUtil.NEW_LINE_HTML_SYMBOL +
                messageUtil.getText(ReplyLocaleText.REPLY_COMMAND_HELP_COMMAND,
                        "1", messageUtil.getText(CommandLocaleText.COMMAND_SHOW_WISHLIST), messageUtil.getText(CommandLocaleText.COMMAND_SHOW_WISHLIST_DESCRIPTION)) + MessageUtil.NEW_LINE_HTML_SYMBOL +
                messageUtil.getText(ReplyLocaleText.REPLY_COMMAND_HELP_COMMAND,
                        "2", messageUtil.getText(CommandLocaleText.COMMAND_ADD_WISHLIST), messageUtil.getText(CommandLocaleText.COMMAND_ADD_WISHLIST_DESCRIPTION)) + MessageUtil.NEW_LINE_HTML_SYMBOL +
                messageUtil.getText(ReplyLocaleText.REPLY_COMMAND_HELP_COMMAND,
                        "3", messageUtil.getText(CommandLocaleText.COMMAND_DELETE_WISHLIST), messageUtil.getText(CommandLocaleText.COMMAND_DELETE_WISHLIST_DESCRIPTION)) + MessageUtil.NEW_LINE_HTML_SYMBOL +
                messageUtil.getText(ReplyLocaleText.REPLY_COMMAND_HELP_COMMAND,
                        "4", messageUtil.getText(CommandLocaleText.COMMAND_IGNORE_DATE), messageUtil.getText(CommandLocaleText.COMMAND_IGNORE_DATE_DESCRIPTION)) + MessageUtil.NEW_LINE_HTML_SYMBOL +
                messageUtil.getText(ReplyLocaleText.REPLY_COMMAND_HELP_COMMAND,
                        "5", messageUtil.getText(CommandLocaleText.COMMAND_NOTIFY_DATE), messageUtil.getText(CommandLocaleText.COMMAND_NOTIFY_DATE_DESCRIPTION)) + MessageUtil.NEW_LINE_HTML_SYMBOL +
                messageUtil.getText(ReplyLocaleText.REPLY_COMMAND_HELP_COMMAND,
                        "6", messageUtil.getText(CommandLocaleText.COMMAND_SHOW_GROUP), messageUtil.getText(CommandLocaleText.COMMAND_SHOW_GROUP_DESCRIPTION)) + MessageUtil.NEW_LINE_HTML_SYMBOL +
                messageUtil.getText(ReplyLocaleText.REPLY_COMMAND_HELP_COMMAND,
                        "7", messageUtil.getText(CommandLocaleText.COMMAND_CREATE_POLL), messageUtil.getText(CommandLocaleText.COMMAND_CREATE_POLL_DESCRIPTION));

        webexFacade.sendDirectMessage(message.getPersonEmail(), reply);
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.HELP;
    }
}
