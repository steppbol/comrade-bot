package com.balashenka.comrade.repository;

import com.balashenka.comrade.model.Space;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SpaceRepository extends JpaRepository<Space, UUID> {
    @NonNull
    @EntityGraph(value = "space.all")
    List<Space> findAllByCreatedDateBefore(ZonedDateTime createdDate);

    @EntityGraph(value = "space.all")
    Space findByRoomId(String roomId);
}
