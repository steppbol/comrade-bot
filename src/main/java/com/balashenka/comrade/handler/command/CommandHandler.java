package com.balashenka.comrade.handler.command;

import com.balashenka.comrade.entity.type.CommandType;
import com.balashenka.comrade.entity.webex.Message;

public interface CommandHandler {
    void execute(Message message);

    CommandType getCommandType();
}
