package com.balashenka.comrade.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Setter
@ConfigurationProperties(prefix = "comrade")
public class ComradeProperty {
    private final WebhookConfiguration webhookConfiguration;
    @Getter
    private Cisco cisco;
    @Getter
    private Date date;
    @Getter
    private Zone zone;
    @Getter
    private Bot bot;
    @Getter
    private Attachment attachment;

    private String webhookHost;

    @Autowired
    public ComradeProperty(WebhookConfiguration webhookConfiguration) {
        this.webhookConfiguration = webhookConfiguration;
    }

    public String getWebhookHost() {
        if (webhookHost == null) {
            webhookHost = webhookConfiguration.getWebhookHost();
        }

        return webhookHost;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Cisco {
        private String token;
        private String api;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Bot {
        private String id;
        private String email;
        private String nickname;
        private String displayName;

        public static boolean checkInformation(Bot bot) {
            boolean result;
            if (bot != null) {
                var idFull = bot.getId() != null && !bot.getId().isBlank();
                var emailFull = bot.getEmail() != null && !bot.getEmail().isBlank();
                var nicknameFull = bot.getNickname() != null && !bot.getNickname().isBlank();
                var displayNameFull = bot.getDisplayName() != null && !bot.getDisplayName().isBlank();
                result = idFull && emailFull && nicknameFull && displayNameFull;
            } else {
                result = false;
            }

            return result;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Date {
        private int creationPeriod;
        private int notificationPeriod;
        private int deletionPeriod;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Zone {
        private String id;
        private String locale;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Attachment {
        private String path;
    }
}
