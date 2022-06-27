package com.balashenka.comrade.job;

import com.balashenka.comrade.configuration.ComradeProperty;
import com.balashenka.comrade.facade.WebexFacade;
import com.balashenka.comrade.service.PollService;
import com.balashenka.comrade.util.AttachmentUtil;
import com.balashenka.comrade.util.MessageUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.balashenka.comrade.util.MessageUtil.NEW_LINE_HTML_SYMBOL;
import static com.balashenka.comrade.util.locale.ReplyLocaleText.REPLY_POLL_FINISH;

@Log4j2
@Component
public class PollJob {
    private final PollService pollService;
    private final ComradeProperty property;
    private final AttachmentUtil attachmentUtil;
    private final MessageUtil messageUtil;
    private final WebexFacade webexFacade;

    @Autowired
    public PollJob(PollService pollService,
                   ComradeProperty property,
                   AttachmentUtil attachmentUtil,
                   MessageUtil messageUtil,
                   WebexFacade webexFacade) {
        this.pollService = pollService;
        this.property = property;
        this.attachmentUtil = attachmentUtil;
        this.messageUtil = messageUtil;
        this.webexFacade = webexFacade;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void checkDate() {
        try {
            deleteExpiredPolls();
        } catch (Exception e) {
            log.error("Error while executing poll job", e);
        }
    }

    private void deleteExpiredPolls() {
        var minusDaysLocalDate = ZonedDateTime.now(ZoneId.of(property.getZone().getId()))
                .minusDays(property.getDate().getDeletionPeriod())
                .toLocalDate()
                .atStartOfDay(ZoneId.of(property.getZone().getId()));

        var polls = pollService.findAllByCreatedDateBefore(minusDaysLocalDate);
        for (var poll : polls) {
            pollService.deleteByMessageId(poll.getMessageId());

            webexFacade.sendMessage(poll.getRoomId(),
                    messageUtil.getText(REPLY_POLL_FINISH, pollService.getByMessageId(poll.getMessageId()).getTitle())
                            + NEW_LINE_HTML_SYMBOL + attachmentUtil.buildFinish(pollService.getResult(poll.getMessageId())));
        }
    }
}
