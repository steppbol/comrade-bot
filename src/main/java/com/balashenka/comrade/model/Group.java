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
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Objects;
import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@NamedEntityGraph(name = "group.all",
        attributeNodes = {
                @NamedAttributeNode(value = "persons")
        }
)
@DynamicUpdate
@Entity
@Table(name = "groups")
public class Group extends BaseEntity {
    @ToString.Include
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @ToString.Include
    @Column(name = "team_id", unique = true, nullable = false)
    private String teamId;

    @OneToMany(mappedBy = "group", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Person> persons;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        var group = (Group) o;
        return getId() != null && Objects.equals(getId(), group.getId())
                && Objects.equals(getName(), group.getName())
                && Objects.equals(getTeamId(), group.getTeamId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId(), getName(), getTeamId());
    }
}
