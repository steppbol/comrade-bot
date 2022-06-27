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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.ZonedDateTime;
import java.util.Objects;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@NamedEntityGraph(name = "space.all",
        attributeNodes = {
                @NamedAttributeNode(value = "person")
        }
)
@DynamicUpdate
@Entity
@Table(name = "spaces")
public class Space extends BaseEntity {
    @ToString.Include
    @Column(name = "created_date", nullable = false, updatable = false)
    private ZonedDateTime createdDate;

    @ToString.Include
    @Column(name = "room_id", nullable = false, unique = true)
    private String roomId;

    @ToString.Include
    @Column(name = "message_id", unique = true)
    private String messageId;

    @ToString.Include
    @Column(name = "title", nullable = false)
    private String title;

    @OneToOne(cascade = {CascadeType.MERGE},
            fetch = FetchType.LAZY,
            optional = false)
    @JoinColumn(name = "person_id", referencedColumnName = "id", nullable = false)
    private Person person;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        var space = (Space) o;
        return getId() != null && Objects.equals(getId(), space.getId())
                && Objects.equals(getMessageId(), space.getMessageId())
                && Objects.equals(getCreatedDate(), space.getCreatedDate())
                && Objects.equals(getRoomId(), space.getRoomId())
                && Objects.equals(getTitle(), space.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId(), getRoomId(), getMessageId(), getCreatedDate(), getTitle());
    }
}
