package com.balashenka.comrade.configuration.impl;

import com.balashenka.comrade.configuration.WebhookConfiguration;
import lombok.Setter;
import ngrok.api.NgrokApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Setter
@Configuration
@ComponentScan(basePackages = {"com.balashenka.comrade"})
@ConditionalOnProperty(prefix = "ngrok", name = "enabled", havingValue = "true")
public class TunnelWebhookConfiguration implements WebhookConfiguration {
    private final NgrokApiClient ngrok;

    @Autowired
    public TunnelWebhookConfiguration(NgrokApiClient ngrok) {
        this.ngrok = ngrok;
    }

    @Override
    public String getWebhookHost() {
        return ngrok.getHttpsTunnelUrl();
    }
}
