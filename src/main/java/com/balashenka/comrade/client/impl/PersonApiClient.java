package com.balashenka.comrade.client.impl;

import com.balashenka.comrade.client.WebexApiClient;
import com.balashenka.comrade.entity.webex.Person;
import org.springframework.stereotype.Component;

@Component
public record PersonApiClient(WebexApiClient client) {
    private static final String PEOPLE_ME_PATH = "/people/me";

    public Person getOwnDetails() {
        return client.get(null, PEOPLE_ME_PATH, null, Person.class);
    }
}
