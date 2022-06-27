package com.balashenka.comrade.configuration.impl;

import com.balashenka.comrade.configuration.WebhookConfiguration;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Setter
@Configuration
@ComponentScan(basePackages = {"com.balashenka.comrade"})
@ConfigurationProperties(prefix = "comrade.web.webhook")
@ConditionalOnProperty(prefix = "ngrok", name = "enabled", havingValue = "false")
public class DefaultWebhookConfiguration implements WebhookConfiguration {
    private String host;

    @Override
    public String getWebhookHost() {
        return host;
    }
}
