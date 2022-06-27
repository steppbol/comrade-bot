package com.balashenka.comrade.client.impl;

import com.balashenka.comrade.client.WebexApiClient;
import com.balashenka.comrade.entity.webex.Team;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public record TeamApiClient(WebexApiClient client) {
    private static final String TEAMS_PATH = "/teams";

    public Team create(Team team) {
        return client.create(team, TEAMS_PATH, Team.class);
    }

    public List<Team> getAll() {
        return client.getAll(TEAMS_PATH, null, Team.class);
    }

    public boolean delete(String id) {
        return client.delete(id, TEAMS_PATH);
    }

}
