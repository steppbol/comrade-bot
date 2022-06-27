package com.balashenka.comrade.bot;

import com.balashenka.comrade.client.impl.PersonApiClient;
import com.balashenka.comrade.client.impl.WebhookApiClient;
import com.balashenka.comrade.configuration.ComradeProperty;
import com.balashenka.comrade.entity.webex.Webhook;
import com.balashenka.comrade.service.TaskService;
import com.balashenka.comrade.util.task.CancelableTask;
import lombok.extern.log4j.Log4j2;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.balashenka.comrade.controller.ApiPath.API_V2_COMRADE_ATTACHMENTS_PATH;
import static com.balashenka.comrade.controller.ApiPath.API_V2_COMRADE_MEMBERSHIPS_PATH;
import static com.balashenka.comrade.controller.ApiPath.API_V2_COMRADE_MESSAGES_PATH;
import static com.balashenka.comrade.exception.Message.BOT_INFORMATION_IS_NULL;

@Log4j2
@Component
public record WebexBot(WebhookApiClient webhookApiClient, PersonApiClient personApiClient,
                       TaskService taskService, ComradeProperty property) {
    private static final String WEBHOOK_MESSAGE_NAME = "messages";
    private static final String WEBHOOK_ATTACHMENT_NAME = "attachments";
    private static final String WEBHOOK_MEMBERSHIP_NAME = "memberships";

    private static final String WEBHOOK_MESSAGE_RESOURCE = "messages";
    private static final String WEBHOOK_ATTACHMENT_RESOURCE = "attachmentActions";
    private static final String WEBHOOK_MEMBERSHIP_RESOURCE = "memberships";

    private static final String WEBHOOK_MESSAGE_EVENT = "created";
    private static final String WEBHOOK_ATTACHMENT_EVENT = "created";
    private static final String WEBHOOK_MEMBERSHIP_EVENT = "created";

    private static final Duration TASK_PERIOD_SECONDS_MILLISECONDS = Duration.ofMillis(1000);

    private static final Map<String, WebhookData> WEBHOOKS = Map.of(
            WEBHOOK_ATTACHMENT_NAME, new WebhookData(WEBHOOK_ATTACHMENT_RESOURCE, WEBHOOK_ATTACHMENT_EVENT, API_V2_COMRADE_ATTACHMENTS_PATH),
            WEBHOOK_MESSAGE_NAME, new WebhookData(WEBHOOK_MESSAGE_RESOURCE, WEBHOOK_MESSAGE_EVENT, API_V2_COMRADE_MESSAGES_PATH),
            WEBHOOK_MEMBERSHIP_NAME, new WebhookData(WEBHOOK_MEMBERSHIP_RESOURCE, WEBHOOK_MEMBERSHIP_EVENT, API_V2_COMRADE_MEMBERSHIPS_PATH)
    );

    @PostConstruct
    private void onStartup() {
        taskService.run(new CancelableTask<String>() {
            @Override
            public String run() {
                return property.getWebhookHost();
            }

            @Override
            public boolean predicate(String result) {
                return result != null && !result.isBlank();
            }

            @Override
            public boolean cancel(UUID taskId) {
                setWebhooks();
                return taskService.cancel(taskId);
            }

        }, TASK_PERIOD_SECONDS_MILLISECONDS);

        var bot = personApiClient.getOwnDetails();
        if (bot != null) {
            var created = new ComradeProperty.Bot(bot.getId(), bot.getEmails().get(0), bot.getNickName(), bot.getDisplayName());
            if (ComradeProperty.Bot.checkInformation(created)) {
                property.setBot(created);
            } else {
                if (ComradeProperty.Bot.checkInformation(property.getBot())) {
                    throw new RuntimeException(BOT_INFORMATION_IS_NULL);
                }
            }
        } else {
            if (ComradeProperty.Bot.checkInformation(property.getBot())) {
                throw new RuntimeException(BOT_INFORMATION_IS_NULL);
            }
        }
    }

    private void setWebhooks() {
        var updatedWebhooks = deleteWebhooks();
        for (var webhookData : updatedWebhooks.entrySet()) {
            var created = webhookApiClient.create(Webhook.builder()
                    .name(webhookData.getKey())
                    .resource(webhookData.getValue().resource())
                    .event(webhookData.getValue().event())
                    .targetUrl(property.getWebhookHost() + webhookData.getValue().url())
                    .build());

            log.info("Set webhook. Name: {}; URL: {}, ID: {}", created.getName(),
                    created.getTargetUrl(), created.getId());
        }
    }

    @NonNull
    private Map<String, WebhookData> deleteWebhooks() {
        Map<String, WebhookData> updatedWebhooks = new HashMap<>();
        Map<String, Webhook> foundWebhooks = new HashMap<>();
        webhookApiClient.getAll().forEach(webhook -> {
            var found = WEBHOOKS.get(webhook.getName());
            String url = "";
            if (found != null) {
                url = property.getWebhookHost() + found.url();
            }

            if (found == null || !url.equals(webhook.getTargetUrl())
                    || !found.resource().equals(webhook.getResource())
                    || !found.event().equals(webhook.getEvent())) {
                var deleted = webhookApiClient.delete(webhook.getId());
                if (deleted) {
                    log.info("Delete webhook. Name: {}; URL: {}, ID: {}", webhook.getName(),
                            webhook.getTargetUrl(), webhook.getId());
                }

                if (found != null) {
                    updatedWebhooks.put(webhook.getName(), found);
                }
            } else {
                foundWebhooks.put(webhook.getName(), webhook);
            }
        });

        for (var webhook : WEBHOOKS.entrySet()) {
            if (!foundWebhooks.containsKey(webhook.getKey())) {
                updatedWebhooks.put(webhook.getKey(), webhook.getValue());
            }
        }

        return updatedWebhooks;
    }

    private record WebhookData(String resource, String event, String url) {
    }
}
