package com.balashenka.comrade.service.impl;

import com.balashenka.comrade.facade.WebexFacade;
import com.balashenka.comrade.model.Person;
import com.balashenka.comrade.model.Space;
import com.balashenka.comrade.service.SpaceService;
import com.balashenka.comrade.configuration.ComradeProperty;
import com.balashenka.comrade.repository.SpaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DefaultSpaceService implements SpaceService {
    private final WebexFacade webexFacade;
    private final SpaceRepository spaceRepository;
    private final ComradeProperty property;

    @Autowired
    public DefaultSpaceService(WebexFacade webexFacade, SpaceRepository spaceRepository, ComradeProperty property) {
        this.webexFacade = webexFacade;
        this.spaceRepository = spaceRepository;
        this.property = property;
    }

    @Override
    public void save(Space space) {
        spaceRepository.save(space);
    }

    @Override
    public void removeMember(String personEmail, String roomId, String membershipId) {
        var foundSpace = spaceRepository.findByRoomId(roomId);
        if (foundSpace != null && foundSpace.getPerson().getEmail().equals(personEmail)) {
            webexFacade.removeRoomMember(membershipId);
        }
    }

    @Override
    public Space create(String title, boolean isLocked, String teamId, Person person, @NonNull List<Person> persons) {
        var roomId = webexFacade.createRoom(title, teamId, isLocked);

        var space = Space.builder()
                .title(title)
                .roomId(roomId)
                .createdDate(ZonedDateTime.now(ZoneId.of(property.getZone().getId()))
                        .toLocalDate()
                        .atStartOfDay(ZoneId.of(property.getZone().getId())))
                .build();

        webexFacade.addRoomMembers(space.getRoomId(), persons.stream().peek(e -> e.setSpace(space)).toList());
        space.setPerson(person);

        save(space);
        return space;
    }

    @Override
    public List<Space> findAllByCreatedDateBefore(ZonedDateTime date) {
        return spaceRepository.findAllByCreatedDateBefore(date);
    }

    @Override
    public Space findByRoomId(String roomId) {
        return spaceRepository.findByRoomId(roomId);
    }

    @Override
    public void delete(UUID spaceId, String roomId) {
        webexFacade.deleteRoom(roomId);
        spaceRepository.deleteById(spaceId);
    }

    @Override
    public void deleteAllById(List<UUID> id) {
        spaceRepository.deleteAllById(id);
    }
}
