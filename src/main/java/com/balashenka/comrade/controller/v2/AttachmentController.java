package com.balashenka.comrade.controller.v2;

import com.balashenka.comrade.controller.ApiPath;
import com.balashenka.comrade.dto.WebhookEventDto;
import com.balashenka.comrade.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPath.API_V2_COMRADE_ATTACHMENTS_PATH)
public class AttachmentController {
    private final AttachmentService attachmentService;

    @Autowired
    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping
    public void attachmentTrigger(@NonNull @RequestBody WebhookEventDto webhookEventDto) {
        var data = webhookEventDto.getData();
        attachmentService.handle(data.getRoomId(), data.getId());
    }
}
