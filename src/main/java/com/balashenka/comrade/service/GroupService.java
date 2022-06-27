package com.balashenka.comrade.service;

import com.balashenka.comrade.model.Group;

import java.io.InputStream;
import java.util.List;

public interface GroupService {
    void synchronize();

    void load(String groupName, InputStream stream);

    InputStream export(String groupName);

    List<Group> findAllByPersonEmail(String personEmail);

    void delete(String groupName);
}
