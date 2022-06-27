package com.balashenka.comrade.service.impl;

import com.balashenka.comrade.service.LocaleService;
import com.balashenka.comrade.configuration.ComradeProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class DefaultLocaleService implements LocaleService {
    private final Locale locale;
    private final MessageSource messageSource;

    @Autowired
    public DefaultLocaleService(MessageSource messageSource, @NonNull ComradeProperty property) {
        this.messageSource = messageSource;
        this.locale = Locale.forLanguageTag(property.getZone().getLocale());
    }

    @Override
    public String getText(String text) {
        return getMessage(text);
    }

    @NonNull
    private String getMessage(String message) {
        return messageSource.getMessage(message, null, locale);
    }
}
