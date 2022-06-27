package com.balashenka.comrade.controller.v2;

import com.balashenka.comrade.controller.ApiPath;
import com.balashenka.comrade.dto.WebhookEventDto;
import com.balashenka.comrade.service.CommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPath.API_V2_COMRADE_MESSAGES_PATH)
public class MessageController {
    private final CommandService commandService;

    @Autowired
    public MessageController(CommandService commandService) {
        this.commandService = commandService;
    }

    @PostMapping
    public void messageTrigger(@NonNull @RequestBody WebhookEventDto webhookEventDto) {
        commandService.handle(webhookEventDto.getData().getPersonEmail(), webhookEventDto.getData().getId());
    }
}
