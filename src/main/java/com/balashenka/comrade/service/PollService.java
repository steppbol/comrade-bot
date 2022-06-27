package com.balashenka.comrade.service;

import com.balashenka.comrade.model.Choice;
import com.balashenka.comrade.model.Poll;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface PollService {
    Poll save(String messageId, String roomId, String title, Set<Choice> choices);

    Poll update(UUID id, Poll poll);

    Map<String, Long> getResult(String messageId);

    void updateChoices(String messageId, List<String> choices);

    Poll getByMessageId(String messageId);

    @Transactional
    List<Poll> findAllByCreatedDateBefore(ZonedDateTime date);

    void deleteByMessageId(String messageId);
}
