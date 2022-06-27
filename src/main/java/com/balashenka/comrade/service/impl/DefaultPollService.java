package com.balashenka.comrade.service.impl;

import com.balashenka.comrade.facade.WebexFacade;
import com.balashenka.comrade.model.Choice;
import com.balashenka.comrade.model.Poll;
import com.balashenka.comrade.service.PollService;
import com.balashenka.comrade.util.mapper.PollMapper;
import com.balashenka.comrade.configuration.ComradeProperty;
import com.balashenka.comrade.repository.PollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class DefaultPollService implements PollService {
    private final PollRepository pollRepository;
    private final WebexFacade webexFacade;
    private final PollMapper pollMapper;
    private final ComradeProperty property;

    @Autowired
    public DefaultPollService(PollRepository pollRepository,
                              WebexFacade webexFacade,
                              PollMapper pollMapper,
                              ComradeProperty property) {
        this.pollRepository = pollRepository;
        this.webexFacade = webexFacade;
        this.pollMapper = pollMapper;
        this.property = property;
    }

    @Override
    @Transactional
    public Poll save(String messageId, String roomId, String title, Set<Choice> choices) {
        return pollRepository.save(Poll.builder()
                .title(title)
                .roomId(roomId)
                .createdDate(ZonedDateTime.now(ZoneId.of(property.getZone().getId()))
                        .toLocalDate()
                        .atStartOfDay(ZoneId.of(property.getZone().getId())))
                .messageId(messageId)
                .choices(choices)
                .build());
    }

    @Override
    @Transactional
    public Poll update(UUID id, Poll poll) {
        var foundPoll = pollRepository.findById(id);

        Poll updatedPoll;
        if (foundPoll.isPresent()) {
            updatedPoll = foundPoll.get();
            pollMapper.updatePatchEntity(poll, updatedPoll);
        } else {
            throw new EntityNotFoundException();
        }

        return pollRepository.save(updatedPoll);
    }

    @Override
    @Transactional
    public Map<String, Long> getResult(String messageId) {
        var foundPoll = pollRepository.findByMessageId(messageId);

        Map<String, Long> result = new HashMap<>();
        if (foundPoll != null) {
            for (var choice : foundPoll.getChoices()) {
                result.put(choice.getChoiceText(), choice.getAmount());
            }
        }

        return result;
    }

    @Override
    @Transactional
    public void updateChoices(String messageId, List<String> choices) {
        var poll = getByMessageId(messageId);
        if (poll != null) {
            for (var pollChoice : poll.getChoices()) {
                for (var choice : choices) {
                    if (pollChoice.getChoiceText().equals(choice)) {
                        pollChoice.setAmount(pollChoice.getAmount() + 1);
                    }
                }
            }

            pollRepository.save(poll);
        }
    }

    @Override
    @Transactional
    public Poll getByMessageId(String messageId) {
        return pollRepository.findByMessageId(messageId);
    }

    @Override
    @Transactional
    public List<Poll> findAllByCreatedDateBefore(ZonedDateTime date) {
        return pollRepository.findAllByCreatedDateBefore(date);
    }

    @Transactional
    @Override
    public void deleteByMessageId(String messageId) {
        pollRepository.deleteByMessageId(messageId);
        webexFacade.deleteMessage(messageId);
    }
}
