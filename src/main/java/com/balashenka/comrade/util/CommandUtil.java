package com.balashenka.comrade.util;

import com.balashenka.comrade.configuration.ComradeProperty;
import com.balashenka.comrade.model.Person;
import com.balashenka.comrade.service.PersonService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.balashenka.comrade.util.locale.CommandLocaleText.COMMAND_ARGUMENT_ALL;

@Component
public record CommandUtil(PersonService personService, MessageUtil messageUtil, ComradeProperty comradeProperty) {
    public static final String COMMAND_SPLIT_REGEX = " ";
    public static final String GROUP_ROOM_TYPE = "group";

    public String fetchArgument(String[] strings, int index) {
        List<String> result = null;
        if (strings != null) {
            result = Arrays.stream(strings)
                    .filter(e -> !e.equals(comradeProperty.getBot().getNickname())).toList();
        }

        return result == null || result.size() < index + 1 ? "" : result.get(index);
    }

    public List<Person> getPersons(@NonNull String groupName, String personEmail) {
        List<Person> persons;

        if (groupName.equals(messageUtil.getText(COMMAND_ARGUMENT_ALL))) {
            persons = personService.findAllByEmail(personEmail);
        } else {
            var found = personService.findByEmailAndGroupName(personEmail, groupName);
            if (found != null) {
                persons = Collections.singletonList(found);
            } else {
                persons = new ArrayList<>();
            }
        }

        return persons;
    }
}
