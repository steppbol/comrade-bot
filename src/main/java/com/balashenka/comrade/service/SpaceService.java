package com.balashenka.comrade.service;

import com.balashenka.comrade.model.Person;
import com.balashenka.comrade.model.Space;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface SpaceService {
    void save(Space space);

    void removeMember(String personEmail, String roomId, String membershipId);

    Space create(String title, boolean isLocked, String teamId, Person person, List<Person> persons);

    List<Space> findAllByCreatedDateBefore(ZonedDateTime date);

    Space findByRoomId(String roomId);

    void delete(UUID spaceId, String roomId);

    void deleteAllById(List<UUID> id);
}
