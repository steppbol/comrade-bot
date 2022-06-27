package com.balashenka.comrade.repository;

import com.balashenka.comrade.model.Group;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {
    @EntityGraph(value = "group.all")
    Group findByName(String name);

    @EntityGraph(value = "group.all")
    Group findByTeamId(String teamId);

    @NonNull
    @EntityGraph(value = "group.all")
    @Query(value = """
            SELECT g
            FROM Group g
            INNER JOIN g.persons p
            WHERE p.email = ?1
            """)
    List<Group> findAllByPersonEmail(String personEmail);
}
