package com.balashenka.comrade.service.impl;

import com.balashenka.comrade.facade.WebexFacade;
import com.balashenka.comrade.model.BaseEntity;
import com.balashenka.comrade.model.Group;
import com.balashenka.comrade.model.Person;
import com.balashenka.comrade.service.GroupService;
import com.balashenka.comrade.service.PersonService;
import com.balashenka.comrade.service.SpaceService;
import com.balashenka.comrade.util.CsvUtil;
import com.balashenka.comrade.util.mapper.PersonMapper;
import com.balashenka.comrade.repository.GroupRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
@Service
public class DefaultGroupService implements GroupService {
    private final WebexFacade webexFacade;
    private final GroupRepository groupRepository;
    private final SpaceService spaceService;
    private final PersonService personService;
    private final PersonMapper personMapper;
    private final CsvUtil csvUtil;
    private final Executor executor;

    @Autowired
    public DefaultGroupService(WebexFacade webexFacade, GroupRepository groupRepository,
                               SpaceService spaceService,
                               PersonService personService,
                               PersonMapper personMapper,
                               CsvUtil csvUtil,
                               @Qualifier("threadPoolTaskExecutor") Executor executor) {
        this.webexFacade = webexFacade;
        this.groupRepository = groupRepository;
        this.spaceService = spaceService;
        this.personService = personService;
        this.personMapper = personMapper;
        this.csvUtil = csvUtil;
        this.executor = executor;
    }

    @Override
    @Transactional
    public void synchronize() {
        var teams = webexFacade.getTeams();

        for (var team : teams) {
            var teamId = team.getId();
            var group = groupRepository.findByTeamId(teamId);
            if (group != null) {
                var members = webexFacade.getTeamMembers(teamId).stream()
                        .map(personMapper::toPerson)
                        .collect(Collectors.toSet());

                update(group, members, false);
            } else {
                webexFacade.deleteTeam(teamId);
            }
        }
    }

    @Override
    @Transactional
    public void load(String groupName, InputStream stream) {
        CompletableFuture
                .runAsync(() -> {
                    log.info("Import group. Name: {}", groupName);
                    var persons = csvUtil.convertFrom(stream);

                    var group = groupRepository.findByName(groupName);
                    if (group != null) {
                        update(group, persons, true);
                    } else {
                        group = Group.builder()
                                .persons(new HashSet<>())
                                .teamId(webexFacade.createTeam(groupName))
                                .name(groupName)
                                .build();

                        addPersonsToGroup(group, persons);

                        groupRepository.save(group);
                    }
                }, executor).exceptionally(e -> {
                    log.error("Error while group importing", e);
                    return null;
                });
    }

    @Override
    public InputStream export(String groupName) {
        log.info("Export group. Name: {}", groupName);
        var found = groupRepository.findByName(groupName);

        InputStream stream;
        if (found != null) {
            stream = csvUtil.convertTo(found.getPersons().stream().toList());
        } else {
            throw new EntityNotFoundException();
        }

        return stream;
    }

    @Override
    public List<Group> findAllByPersonEmail(String personEmail) {
        return groupRepository.findAllByPersonEmail(personEmail);
    }

    @Transactional
    @Override
    public void delete(String groupName) {
        var found = groupRepository.findByName(groupName);

        if (found != null) {
            webexFacade.deleteTeam(found.getTeamId());
            groupRepository.deleteById(found.getId());
        } else {
            throw new EntityNotFoundException();
        }
    }

    private void update(@NonNull Group group, @NonNull Set<Person> persons, boolean updateExisting) {
        var groupPersons = group.getPersons();

        var removed = groupPersons.stream()
                .filter(e -> persons.stream().noneMatch(x -> e.getEmail().equals(x.getEmail())))
                .collect(Collectors.toSet());

        var added = persons.stream()
                .filter(e -> groupPersons.stream().noneMatch(x -> e.getEmail().equals(x.getEmail())))
                .collect(Collectors.toSet());

        if (removed.size() > 0) {
            var removedFromTeam = removed.stream()
                    .filter(e -> persons.stream().anyMatch(x -> e.getEmail().equals(x.getEmail())))
                    .collect(Collectors.toSet());

            removePersonsFromGroup(group, removed, removedFromTeam);
        }

        if (updateExisting) {
            var updated = persons.stream()
                    .filter(e -> groupPersons.stream().anyMatch(x -> e.getEmail().equals(x.getEmail())))
                    .collect(Collectors.toSet());

            if (updated.size() > 0) {
                updatePersonsInGroup(group, updated);
            }
        }

        if (added.size() > 0) {
            addPersonsToGroup(group, added);
        }

        groupRepository.save(group);
    }

    private void addPersonsToGroup(@NonNull Group group, @NonNull Set<Person> persons) {
        var updated = Stream.concat(group.getPersons().stream(),
                        persons.stream()
                                .peek(e -> e.setTeamMembershipId(webexFacade.addTeamMember(group.getTeamId(), e.getEmail(), e.isModerator()).getId())))
                .collect(Collectors.toSet());

        group.setPersons(updated);
        updated.forEach(e -> e.setGroup(group));
    }

    private void removePersonsFromGroup(@NonNull Group group, @NonNull Set<Person> persons, @NonNull Set<Person> personsFromTeam) {
        var updated = group.getPersons().stream()
                .filter(e -> persons.stream()
                        .noneMatch(x -> e.getEmail().equals(x.getEmail())))
                .collect(Collectors.toSet());

        spaceService.deleteAllById(persons.stream()
                .map(Person::getSpace)
                .filter(Objects::nonNull)
                .map(BaseEntity::getId)
                .toList());

        personService.deleteAllById(persons.stream()
                .map(BaseEntity::getId)
                .toList());

        webexFacade.removeTeamMembers(personsFromTeam.stream()
                .map(Person::getTeamMembershipId)
                .toList());

        group.setPersons(updated);
        updated.forEach(e -> e.setGroup(group));
    }

    private void updatePersonsInGroup(@NonNull Group group, Set<Person> persons) {
        for (var groupPerson : group.getPersons()) {
            for (var person : persons) {
                if (groupPerson.getEmail().equals(person.getEmail())) {
                    personMapper.updatePatchEntity(person, groupPerson);
                }
            }
        }
    }
}
