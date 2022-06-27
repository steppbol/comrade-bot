package com.balashenka.comrade.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@NamedEntityGraph(name = "poll.all",
        attributeNodes = {
                @NamedAttributeNode(value = "choices")
        }
)
@DynamicUpdate
@Entity
@Table(name = "polls")
public class Poll extends BaseEntity {
    @Column(name = "title")
    private String title;

    @ToString.Include
    @Column(name = "created_date", nullable = false, updatable = false)
    private ZonedDateTime createdDate;

    @ToString.Include
    @Column(name = "room_id", nullable = false)
    private String roomId;

    @ToString.Include
    @Column(name = "message_id", nullable = false, unique = true)
    private String messageId;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @JoinColumn(name = "poll_id", nullable = false)
    private Set<Choice> choices;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Poll poll = (Poll) o;
        return getId() != null && Objects.equals(getId(), poll.getId())
                && Objects.equals(getMessageId(), poll.getMessageId())
                && Objects.equals(getTitle(), poll.getTitle())
                && Objects.equals(getCreatedDate(), poll.getCreatedDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId(), getTitle(), getCreatedDate(), getMessageId());
    }
}
