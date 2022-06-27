package com.balashenka.comrade.model;

import lombok.AllArgsConstructor;
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
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@NamedEntityGraph(name = "person.all",
        attributeNodes = {
                @NamedAttributeNode(value = "space"),
                @NamedAttributeNode(value = "group", subgraph = "group.all")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "group.all",
                        attributeNodes = {
                                @NamedAttributeNode("persons")
                        }
                )
        }
)
@DynamicUpdate
@Entity
@Table(name = "persons")
public class Person extends BaseEntity {
    @Max(31)
    @Min(1)
    @ToString.Include
    @Column(name = "day")
    private int day;

    @Max(12)
    @Min(1)
    @ToString.Include
    @Column(name = "month")
    private int month;

    @ToString.Include
    @Column(name = "name", nullable = false)
    private String name;

    @Email
    @ToString.Include
    @Column(name = "email", nullable = false)
    private String email;

    @ToString.Include
    @Column(name = "is_moderator", nullable = false)
    private boolean isModerator;

    @ToString.Include
    @Column(name = "team_membership_id", unique = true, nullable = false)
    private String teamMembershipId;

    @ToString.Include
    @Column(name = "wishlist")
    private String wishlist;

    @ToString.Include
    @Column(name = "is_ignoring", nullable = false)
    private boolean isIgnoring;

    @ToString.Include
    @Column(name = "is_notified", nullable = false)
    private boolean isNotified;

    @OneToOne(mappedBy = "person", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private Space space;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY,
            optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        var person = (Person) o;
        return getId() != null && Objects.equals(getId(), person.getId())
                && Objects.equals(getDay(), person.getDay())
                && Objects.equals(getMonth(), person.getMonth())
                && Objects.equals(getName(), person.getName())
                && Objects.equals(getEmail(), person.getEmail())
                && Objects.equals(isModerator(), person.isModerator())
                && Objects.equals(getTeamMembershipId(), person.getTeamMembershipId())
                && Objects.equals(getWishlist(), person.getWishlist())
                && Objects.equals(isIgnoring(), person.isIgnoring())
                && Objects.equals(isNotified(), person.isNotified());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId(), getDay(), getMonth(), getName(), getEmail(), isModerator(),
                getTeamMembershipId(), getWishlist(), isIgnoring(), isNotified());
    }
}
