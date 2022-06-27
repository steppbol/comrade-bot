package com.balashenka.comrade.service.impl;

import com.balashenka.comrade.model.Person;
import com.balashenka.comrade.service.PersonService;
import com.balashenka.comrade.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DefaultPersonService implements PersonService {
    private static final String NEW_LINE_REGEX = "[\\n]+";
    private static final String NEW_LINE_SYMBOL = "\\\n";
    private final PersonRepository personRepository;

    @Autowired
    public DefaultPersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public void save(@NonNull Person person) {
        var wishlist = person.getWishlist();
        if (wishlist != null && !wishlist.isBlank()) {
            person.setWishlist(wishlist.replaceAll(NEW_LINE_REGEX, NEW_LINE_SYMBOL));
        }
        personRepository.save(person);
    }

    @Override
    public List<Person> findAllByEmail(String email) {
        return personRepository.findAllByEmail(email);
    }

    @Override
    public Person findByEmailAndGroupName(String email, String groupName) {
        return personRepository.findByEmailAndGroupName(email, groupName);
    }

    @Override
    public List<Person> findAllNotNotifiedByDateBetween(@NonNull ZonedDateTime firstDate, @NonNull ZonedDateTime secondDate) {
        return personRepository.findAllNotNotifiedByDateBetween(firstDate.getDayOfMonth(), firstDate.getMonth().getValue(),
                secondDate.getDayOfMonth(), secondDate.getMonth().getValue());
    }

    @Override
    public List<Person> findAllWithoutSpacesByDateBetween(@NonNull ZonedDateTime firstDate, @NonNull ZonedDateTime secondDate) {
        return personRepository.findAllWithoutSpacesByDateBetween(firstDate.getDayOfMonth(), firstDate.getMonth().getValue(),
                secondDate.getDayOfMonth(), secondDate.getMonth().getValue());
    }

    @Override
    public void deleteAllById(List<UUID> id) {
        personRepository.deleteAllById(id);
    }
}
