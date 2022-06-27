package com.balashenka.comrade.repository;

import com.balashenka.comrade.model.Person;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PersonRepository extends JpaRepository<Person, UUID> {
    @NonNull
    @EntityGraph(value = "person.all")
    @Query(value = """
            SELECT p
            FROM Person p
            WHERE (p.day >= ?1 AND p.month >= ?2 OR p.day < ?1 AND p.month < ?2 OR p.day < ?1 AND p.month > ?2)
            AND (p.day <= ?3 AND p.month = ?4 OR p.day > ?3 AND p.month < ?4)
            AND p.isNotified = false AND p.isIgnoring = false
            """)
    List<Person> findAllNotNotifiedByDateBetween(int firstDay, int firstMonth, int secondDay, int secondMonth);

    @NonNull
    @EntityGraph(value = "person.all")
    @Query(value = """
            SELECT p
            FROM Person p
            LEFT OUTER JOIN p.space s
            WHERE (p.day >= ?1 AND p.month >= ?2 OR p.day < ?1 AND p.month < ?2 OR p.day < ?1 AND p.month > ?2)
            AND (p.day <= ?3 AND p.month = ?4 OR p.day > ?3 AND p.month < ?4)
            AND p.isIgnoring = false AND s IS NULL
            """)
    List<Person> findAllWithoutSpacesByDateBetween(int firstDay, int firstMonth, int secondDay, int secondMonth);

    @NonNull
    @EntityGraph(value = "person.all")
    List<Person> findAllByEmail(String email);

    @NonNull
    @EntityGraph(value = "person.all")
    @Query(value = """
            SELECT p
            FROM Person p
            WHERE p.email = ?1 AND p.group.name = ?2
            """)
    Person findByEmailAndGroupName(String email, String groupName);
}
