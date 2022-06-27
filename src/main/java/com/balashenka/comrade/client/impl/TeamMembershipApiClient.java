package com.balashenka.comrade.client.impl;

import com.balashenka.comrade.client.WebexApiClient;
import com.balashenka.comrade.entity.webex.TeamMembership;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public record TeamMembershipApiClient(WebexApiClient client) {
    private static final String TEAM_MEMBERSHIPS_PATH = "/team/memberships";

    private static final String TEAM_ID_QUERY_PARAM = "teamId";

    public TeamMembership create(TeamMembership teamMembership) {
        return client.create(teamMembership, TEAM_MEMBERSHIPS_PATH, TeamMembership.class);
    }

    public List<TeamMembership> getAll(String teamId) {
        return client.getAll(TEAM_MEMBERSHIPS_PATH, Map.of(TEAM_ID_QUERY_PARAM, teamId), TeamMembership.class);
    }

    public boolean delete(String id) {
        return client.delete(id, TEAM_MEMBERSHIPS_PATH);
    }
}
