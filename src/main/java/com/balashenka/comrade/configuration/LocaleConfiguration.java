package com.balashenka.comrade.configuration;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
@ComponentScan(basePackages = {"com.balashenka.comrade"})
public class LocaleConfiguration {
    @Bean
    public MessageSource messageSource() {
        var messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:locale/messages/messages");
        messageSource.setCacheSeconds(60);
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}