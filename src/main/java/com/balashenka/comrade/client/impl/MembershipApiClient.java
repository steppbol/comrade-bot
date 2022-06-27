package com.balashenka.comrade.client.impl;

import com.balashenka.comrade.client.WebexApiClient;
import com.balashenka.comrade.entity.webex.Membership;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public record MembershipApiClient(WebexApiClient client) {
    private static final String MEMBERSHIPS_PATH = "/memberships";

    private static final String ROOM_ID_QUERY_PARAM = "roomId";
    private static final String PERSON_ID_QUERY_PARAM = "personId";

    public Membership create(Membership membership) {
        return client.create(membership, MEMBERSHIPS_PATH, Membership.class);
    }

    public Membership get(String roomId, String personId) {
        return client.get(null, MEMBERSHIPS_PATH,
                Map.of(ROOM_ID_QUERY_PARAM, roomId, PERSON_ID_QUERY_PARAM, personId), Membership.class);
    }

    public boolean delete(String id) {
        return client.delete(id, MEMBERSHIPS_PATH);
    }
}
