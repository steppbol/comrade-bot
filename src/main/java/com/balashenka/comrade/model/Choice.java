package com.balashenka.comrade.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@DynamicUpdate
@Entity
@Table(name = "choices")
public class Choice extends BaseEntity {
    @ToString.Include
    @Column(name = "amount", nullable = false)
    private Long amount;

    @ToString.Include
    @Column(name = "choice_text", nullable = false)
    private String choiceText;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Choice choice = (Choice) o;
        return getId() != null && Objects.equals(getId(), choice.getId())
                && Objects.equals(getAmount(), choice.getAmount())
                && Objects.equals(getChoiceText(), choice.getChoiceText());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId(), getAmount(), getChoiceText());
    }
}
