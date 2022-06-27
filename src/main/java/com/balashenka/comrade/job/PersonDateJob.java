package com.balashenka.comrade.job;

import com.balashenka.comrade.configuration.ComradeProperty;
import com.balashenka.comrade.entity.type.AttachmentType;
import com.balashenka.comrade.facade.WebexFacade;
import com.balashenka.comrade.factory.AttachmentFactoryProvider;
import com.balashenka.comrade.service.GroupService;
import com.balashenka.comrade.service.PersonService;
import com.balashenka.comrade.service.SpaceService;
import com.balashenka.comrade.util.MessageUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.balashenka.comrade.util.locale.AttachmentLocaleText.ATTACHMENT_WISHLIST_UPDATE_BUTTON_UPDATE_REPLACE;
import static com.balashenka.comrade.util.locale.AttachmentLocaleText.ATTACHMENT_WISHLIST_UPDATE_LINK_MESSAGE_TEXT_OLD_WISHLIST;
import static com.balashenka.comrade.util.locale.AttachmentLocaleText.ATTACHMENT_WISHLIST_UPDATE_MESSAGE_TEXT;
import static com.balashenka.comrade.util.locale.AttachmentLocaleText.ATTACHMENT_WISHLIST_UPDATE_MESSAGE_TEXT_NEW_WISHLIST;
import static com.balashenka.comrade.util.locale.AttachmentLocaleText.ATTACHMENT_WISHLIST_UPDATE_TEXT_MESSAGE_TEXT_OLD_WISHLIST;
import static com.balashenka.comrade.util.locale.AttachmentLocaleText.ATTACHMENT_WISHLIST_UPDATE_UPDATE_REPLACE_BUTTON_DELETE_TEXT;
import static com.balashenka.comrade.util.locale.AttachmentLocaleText.ATTACHMENT_WISHLIST_UPDATE_UPDATE_REPLACE_BUTTON_SAVE_TEXT;
import static com.balashenka.comrade.util.locale.AttachmentLocaleText.ATTACHMENT_WISHLIST_UPDATE_UPDATE_REPLACE_INPUT_TEXT;
import static com.balashenka.comrade.util.locale.AttachmentLocaleText.ATTACHMENT_WISHLIST_UPDATE_UPDATE_REPLACE_MESSAGE_TEXT;
import static com.balashenka.comrade.util.locale.ReplyLocaleText.REPLY_GROUP_WISHLIST_EMPTY_TEXT;
import static com.balashenka.comrade.util.locale.ReplyLocaleText.REPLY_GROUP_WISHLIST_LINK_TEXT;
import static com.balashenka.comrade.util.locale.ReplyLocaleText.REPLY_GROUP_WISHLIST_TEXT_TEXT;
import static com.balashenka.comrade.util.locale.SpaceLocaleText.SPACE_NAME_PREFIX;

@Log4j2
@Component
public class PersonDateJob {
    private static final String DOT_SYMBOL = ".";

    private final AttachmentFactoryProvider attachmentFactoryProvider;
    private final GroupService groupService;
    private final PersonService personService;
    private final SpaceService spaceService;
    private final MessageUtil messageUtil;
    private final WebexFacade webexFacade;
    private final ComradeProperty property;

    @Autowired
    public PersonDateJob(AttachmentFactoryProvider attachmentFactoryProvider,
                         GroupService groupService,
                         PersonService personService,
                         SpaceService spaceService,
                         MessageUtil messageUtil,
                         WebexFacade webexFacade, ComradeProperty property) {
        this.attachmentFactoryProvider = attachmentFactoryProvider;
        this.groupService = groupService;
        this.personService = personService;
        this.spaceService = spaceService;
        this.messageUtil = messageUtil;
        this.webexFacade = webexFacade;
        this.property = property;
    }

    @PostConstruct
    private void onStartup() {
        checkDate();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void checkDate() {
        try {
            groupService.synchronize();

            notifyPersons();

            createSpaces();

            deleteExpiredSpaces();
        } catch (Exception e) {
            log.error("Error while executing person date job", e);
        }
    }

    private void notifyPersons() {
        var firstDate = ZonedDateTime.now(ZoneId.of(property.getZone().getId()))
                .minusDays(property.getDate().getDeletionPeriod())
                .toLocalDate()
                .atStartOfDay(ZoneId.of(property.getZone().getId()));

        var secondDate = ZonedDateTime.now(ZoneId.of(property.getZone().getId()))
                .plusDays(property.getDate().getNotificationPeriod())
                .toLocalDate()
                .atStartOfDay(ZoneId.of(property.getZone().getId()));

        var persons = personService.findAllNotNotifiedByDateBetween(firstDate, secondDate);

        for (var person : persons) {
            String wishlistMessage;
            if (person.getWishlist() != null && !person.getWishlist().isBlank()) {
                if (messageUtil.isUrl(person.getWishlist())) {
                    wishlistMessage = messageUtil.getText(ATTACHMENT_WISHLIST_UPDATE_LINK_MESSAGE_TEXT_OLD_WISHLIST, person.getWishlist());
                } else {
                    wishlistMessage = messageUtil.getText(ATTACHMENT_WISHLIST_UPDATE_TEXT_MESSAGE_TEXT_OLD_WISHLIST, person.getWishlist());
                }
            } else {
                wishlistMessage = messageUtil.getText(ATTACHMENT_WISHLIST_UPDATE_MESSAGE_TEXT_NEW_WISHLIST);
            }

            var text = messageUtil.getText(ATTACHMENT_WISHLIST_UPDATE_MESSAGE_TEXT, person.getGroup().getName(), wishlistMessage);

            attachmentFactoryProvider.getFactory(AttachmentType.UPDATE_WISHLIST)
                    .create(person.getEmail(),
                            person.getEmail(),
                            person.getGroup().getName(),
                            text,
                            messageUtil.getText(ATTACHMENT_WISHLIST_UPDATE_BUTTON_UPDATE_REPLACE),
                            messageUtil.getText(ATTACHMENT_WISHLIST_UPDATE_UPDATE_REPLACE_MESSAGE_TEXT),
                            messageUtil.getText(ATTACHMENT_WISHLIST_UPDATE_UPDATE_REPLACE_INPUT_TEXT),
                            messageUtil.getText(ATTACHMENT_WISHLIST_UPDATE_UPDATE_REPLACE_BUTTON_SAVE_TEXT),
                            messageUtil.getText(ATTACHMENT_WISHLIST_UPDATE_UPDATE_REPLACE_BUTTON_DELETE_TEXT)
                    );
            person.setNotified(true);
            personService.save(person);
        }
    }

    private void createSpaces() {
        var firstDate = ZonedDateTime.now(ZoneId.of(property.getZone().getId()))
                .minusDays(property.getDate().getDeletionPeriod())
                .toLocalDate()
                .atStartOfDay(ZoneId.of(property.getZone().getId()));

        var secondDate = ZonedDateTime.now(ZoneId.of(property.getZone().getId()))
                .plusDays(property.getDate().getCreationPeriod())
                .toLocalDate()
                .atStartOfDay(ZoneId.of(property.getZone().getId()));

        var foundPersons = personService.findAllWithoutSpacesByDateBetween(firstDate, secondDate);

        for (var foundPerson : foundPersons) {
            var group = foundPerson.getGroup();

            var persons = group.getPersons().stream()
                    .filter(e -> !e.getEmail().equals(foundPerson.getEmail()))
                    .filter(e -> !e.isIgnoring())
                    .toList();

            var title = messageUtil.getText(SPACE_NAME_PREFIX) + " " + foundPerson.getName() +
                    " " + foundPerson.getDay() + DOT_SYMBOL + foundPerson.getMonth();

            var space = spaceService.create(title, true, group.getTeamId(), foundPerson, persons);

            String text;
            if (messageUtil.isUrl(foundPerson.getWishlist())) {
                text = messageUtil.getText(REPLY_GROUP_WISHLIST_LINK_TEXT, foundPerson.getName(), foundPerson.getEmail(), foundPerson.getWishlist());
            } else {
                if (foundPerson.getWishlist() != null && !foundPerson.getWishlist().isBlank()) {
                    text = messageUtil.getText(REPLY_GROUP_WISHLIST_TEXT_TEXT, foundPerson.getName(), foundPerson.getEmail(), foundPerson.getWishlist());
                } else {
                    text = messageUtil.getText(REPLY_GROUP_WISHLIST_EMPTY_TEXT, foundPerson.getName(), foundPerson.getEmail());
                }
            }

            var created = webexFacade.sendMessage(space.getRoomId(), text);

            space.setMessageId(created.getId());
            spaceService.save(space);
        }
    }

    private void deleteExpiredSpaces() {
        var minusDaysLocalDate = ZonedDateTime.now(ZoneId.of(property.getZone().getId()))
                .minusDays(property.getDate().getDeletionPeriod())
                .toLocalDate()
                .atStartOfDay(ZoneId.of(property.getZone().getId()));

        var spaces = spaceService.findAllByCreatedDateBefore(minusDaysLocalDate);
        for (var space : spaces) {
            var person = space.getPerson();
            person.setNotified(false);
            personService.save(person);
            spaceService.delete(space.getId(), space.getRoomId());
        }
    }
}
