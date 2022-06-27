package com.balashenka.comrade.handler.command.impl;

import com.balashenka.comrade.entity.type.CommandType;
import com.balashenka.comrade.entity.webex.Message;
import com.balashenka.comrade.facade.WebexFacade;
import com.balashenka.comrade.util.CommandUtil;
import com.balashenka.comrade.util.MessageUtil;
import com.balashenka.comrade.util.locale.CommandLocaleText;
import com.balashenka.comrade.util.locale.ReplyLocaleText;
import com.balashenka.comrade.handler.command.CommandHandler;
import com.balashenka.comrade.service.SpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ShowWishlistCommandHandler implements CommandHandler {
    private final WebexFacade webexFacade;
    private final CommandUtil commandUtil;
    private final SpaceService spaceService;
    private final MessageUtil messageUtil;

    @Autowired
    public ShowWishlistCommandHandler(WebexFacade webexFacade,
                                      CommandUtil commandUtil,
                                      SpaceService spaceService,
                                      MessageUtil messageUtil) {
        this.webexFacade = webexFacade;
        this.commandUtil = commandUtil;
        this.spaceService = spaceService;
        this.messageUtil = messageUtil;
    }

    @Override
    public void execute(@NonNull Message message) {
        if (message.getRoomType().equals(CommandUtil.GROUP_ROOM_TYPE)) {
            executeGroup(message);
        } else {
            executeDirect(message);
        }
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.SHOW_WISHLIST;
    }

    private void executeGroup(@NonNull Message message) {
        var space = spaceService.findByRoomId(message.getRoomId());
        if (space != null) {
            var wishlist = space.getPerson().getWishlist();

            if (wishlist != null && !wishlist.isBlank()) {
                if (messageUtil.isUrl(wishlist)) {
                    webexFacade.sendDirectMessage(message.getPersonEmail(),
                            messageUtil.getText(ReplyLocaleText.REPLY_COMMAND_SHOW_WISHLIST_LINK_FOUND, space.getPerson().getWishlist(), space.getTitle()));
                } else {
                    webexFacade.sendDirectMessage(message.getPersonEmail(),
                            messageUtil.getText(ReplyLocaleText.REPLY_COMMAND_SHOW_WISHLIST_TEXT_FOUND, space.getTitle(), space.getPerson().getWishlist()));
                }
            } else {
                webexFacade.sendDirectMessage(message.getPersonEmail(), messageUtil.getText(ReplyLocaleText.REPLY_COMMAND_SHOW_WISHLIST_GROUP_EMPTY, space.getTitle()));
            }
        } else {
            executeDirect(message);
        }
    }

    private void executeDirect(@NonNull Message message) {
        var text = message.getText().trim().split(CommandUtil.COMMAND_SPLIT_REGEX);

        if (text.length >= 2) {
            var groupName = commandUtil.fetchArgument(text, 1);
            var persons = commandUtil.getPersons(groupName, message.getPersonEmail());

            if (persons.size() > 0) {
                var result = new StringBuilder();

                for (var person : persons) {
                    String reply;
                    var foundWishlist = person.getWishlist();
                    if (foundWishlist != null && !foundWishlist.isBlank() && messageUtil.isUrl(foundWishlist)) {
                        reply = messageUtil.getText(ReplyLocaleText.REPLY_COMMAND_SHOW_WISHLIST_LINK_FOUND,
                                person.getWishlist(), person.getGroup().getName());
                    } else if (foundWishlist != null && !foundWishlist.isBlank()) {
                        reply = messageUtil.getText(ReplyLocaleText.REPLY_COMMAND_SHOW_WISHLIST_TEXT_FOUND,
                                person.getGroup().getName(), person.getWishlist());
                    } else {
                        reply = messageUtil.getText(ReplyLocaleText.REPLY_COMMAND_SHOW_WISHLIST_DIRECT_EMPTY,
                                messageUtil.getText(CommandLocaleText.COMMAND_ADD_WISHLIST));
                    }

                    result.append(reply).append(MessageUtil.NEW_LINE_HTML_SYMBOL);
                }

                webexFacade.sendDirectMessage(message.getPersonEmail(), result.toString());
            } else {
                webexFacade.sendDirectMessage(message.getPersonEmail(), messageUtil.getText(ReplyLocaleText.REPLY_COMMAND_SHOW_WISHLIST_DIRECT_EMPTY,
                        messageUtil.getText(CommandLocaleText.COMMAND_ADD_WISHLIST)));
            }
        } else {
            webexFacade.sendDirectMessage(message.getPersonEmail(), messageUtil.getText(ReplyLocaleText.REPLY_COMMAND_SHOW_WISHLIST_BAD_FORMAT,
                    messageUtil.getText(CommandLocaleText.COMMAND_SHOW_WISHLIST), messageUtil.getText(CommandLocaleText.COMMAND_ARGUMENT_ALL)));
        }
    }
}
