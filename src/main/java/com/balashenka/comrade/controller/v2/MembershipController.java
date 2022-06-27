package com.balashenka.comrade.controller.v2;

import com.balashenka.comrade.controller.ApiPath;
import com.balashenka.comrade.dto.WebhookEventDto;
import com.balashenka.comrade.service.SpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPath.API_V2_COMRADE_MEMBERSHIPS_PATH)
public class MembershipController {
    private final SpaceService spaceService;

    @Autowired
    public MembershipController(SpaceService spaceService) {
        this.spaceService = spaceService;
    }

    @PostMapping
    public void membershipTrigger(@NonNull @RequestBody WebhookEventDto webhookEventDto) {
        spaceService.removeMember(webhookEventDto.getData().getPersonEmail(), webhookEventDto.getData().getRoomId(), webhookEventDto.getData().getId());
    }
}
