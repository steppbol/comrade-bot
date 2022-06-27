package com.balashenka.comrade.repository;

import com.balashenka.comrade.model.Poll;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PollRepository extends JpaRepository<Poll, UUID> {
    @EntityGraph(value = "poll.all")
    Poll findByMessageId(String messageId);

    @NonNull
    @EntityGraph(value = "poll.all")
    List<Poll> findAllByCreatedDateBefore(ZonedDateTime createdDate);

    void deleteByMessageId(String messageId);
}
