package com.balashenka.comrade.facade;

import com.balashenka.comrade.client.impl.AttachmentActionApiClient;
import com.balashenka.comrade.client.impl.MembershipApiClient;
import com.balashenka.comrade.client.impl.MessageApiClient;
import com.balashenka.comrade.client.impl.RoomApiClient;
import com.balashenka.comrade.client.impl.TeamApiClient;
import com.balashenka.comrade.client.impl.TeamMembershipApiClient;
import com.balashenka.comrade.entity.webex.Message;
import com.balashenka.comrade.entity.webex.Team;
import com.balashenka.comrade.model.Person;
import com.balashenka.comrade.configuration.ComradeProperty;
import com.balashenka.comrade.entity.webex.AttachmentAction;
import com.balashenka.comrade.entity.webex.Membership;
import com.balashenka.comrade.entity.webex.Room;
import com.balashenka.comrade.entity.webex.TeamMembership;
import lombok.extern.log4j.Log4j2;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Log4j2
@Component
public record WebexFacade(RoomApiClient roomApiClient,
                          MembershipApiClient membershipApiClient,
                          MessageApiClient messageApiClient,
                          TeamApiClient teamApiClient,
                          TeamMembershipApiClient teamMembershipApiClient,
                          AttachmentActionApiClient attachmentActionApiClient,
                          ComradeProperty property) {
    public String createRoom(String title, String teamId, boolean isLocked) {
        var created = roomApiClient.create(Room.builder()
                .title(title)
                .teamId(teamId)
                .isLocked(isLocked)
                .build());

        log.info("Create room. Title: {}, ID: {}", created.getTitle(), created.getId());
        return created.getId();
    }

    public void deleteRoom(String id) {
        log.info("Delete room. ID: {}", id);
        roomApiClient.delete(id);
    }

    public void addRoomMembers(String roomId, @NonNull List<Person> persons) {
        log.info("Add members to room. Room ID: {}", roomId);
        persons.forEach(e -> membershipApiClient.create(Membership.builder()
                .roomId(roomId)
                .isModerator(e.isModerator())
                .personEmail(e.getEmail())
                .build()));
    }

    public void removeRoomMember(String membershipId) {
        log.info("Remove member from room. Membership ID: {}", membershipId);
        membershipApiClient.delete(membershipId);
    }

    public Message sendMessage(String roomId, String text) {
        log.info("Send message. room ID: {}", roomId);

        return messageApiClient.create(Message.builder()
                .roomId(roomId)
                .markdown(text)
                .build());
    }

    public Message sendMessage(String roomId, String text, List<Map<String, ?>> attachments) {
        log.info("Send message. room ID: {}", roomId);

        return messageApiClient.create(Message.builder()
                .roomId(roomId)
                .markdown(text)
                .attachments(attachments)
                .build());
    }

    public Message sendDirectMessage(String email, String text, List<Map<String, ?>> attachments) {
        log.info("Send direct message");

        return messageApiClient.create(Message.builder()
                .toPersonEmail(email)
                .markdown(text)
                .attachments(attachments)
                .build());
    }

    public void sendDirectMessage(String email, String text) {
        log.info("Send direct message");

        messageApiClient.create(Message.builder()
                .toPersonEmail(email)
                .markdown(text)
                .build());
    }

    public Message getMessage(String id) {
        return messageApiClient.get(id);
    }

    public void deleteMessage(String id) {
        log.info("Delete message. ID: {}", id);

        messageApiClient.delete(id);
    }

    public String createTeam(String name) {
        var created = teamApiClient.create(Team.builder()
                .name(name)
                .build());

        log.info("Create team. Title: {}, ID: {}", created.getName(), created.getId());
        return created.getId();
    }

    @NonNull
    public List<Team> getTeams() {
        var teams = teamApiClient.getAll();
        log.info("Get teams. Amount: {}", teams.size());
        return teams;
    }

    public void deleteTeam(String teamId) {
        log.info("Delete team. ID: {}", teamId);

        teamApiClient.delete(teamId);
    }

    public TeamMembership addTeamMember(String teamId, String email, boolean isModerator) {
        log.info("Add member to team. Team ID: {}", teamId);

        return teamMembershipApiClient.create(TeamMembership.builder()
                .teamId(teamId)
                .personEmail(email)
                .isModerator(isModerator)
                .build());
    }

    public List<TeamMembership> getTeamMembers(String teamId) {
        log.info("Get team members. Team ID: {}", teamId);
        return teamMembershipApiClient.getAll(teamId).stream()
                .filter(e -> !e.getPersonEmail().equals(property.getBot().getEmail()))
                .toList();
    }

    public void removeTeamMembers(@NonNull List<String> id) {
        log.info("Delete members from team");
        id.forEach(teamMembershipApiClient::delete);
    }

    public AttachmentAction getAttachmentAction(String id) {
        log.info("Get attachment action. ID: {}", id);
        return attachmentActionApiClient.get(id);
    }
}
