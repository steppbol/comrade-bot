package com.balashenka.comrade.service;

import com.balashenka.comrade.model.Person;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface PersonService {
    void save(Person person);

    List<Person> findAllByEmail(String email);

    Person findByEmailAndGroupName(String email, String groupName);

    List<Person> findAllNotNotifiedByDateBetween(ZonedDateTime firstDate, ZonedDateTime secondDate);

    List<Person> findAllWithoutSpacesByDateBetween(ZonedDateTime firstDate, ZonedDateTime secondDate);

    void deleteAllById(List<UUID> id);
}
