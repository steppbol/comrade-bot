package com.balashenka.comrade.util.mapper;

import com.balashenka.comrade.entity.webex.TeamMembership;
import com.balashenka.comrade.model.Person;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface PersonMapper {
    @Mappings({
            @Mapping(target = "notified", source = "notified"),
            @Mapping(target = "moderator", source = "moderator"),
            @Mapping(target = "ignoring", source = "ignoring")
    })
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePatchEntity(Person source, @MappingTarget Person target);

    @Mappings({
            @Mapping(target = "name", source = "teamMembership.personDisplayName"),
            @Mapping(target = "email", source = "teamMembership.personEmail"),
            @Mapping(target = "ignoring", constant = "true"),
            @Mapping(target = "notified", constant = "true"),
            @Mapping(target = "moderator", source = "teamMembership.isModerator"),
            @Mapping(target = "day", ignore = true),
            @Mapping(target = "group", ignore = true),
            @Mapping(target = "month", ignore = true),
            @Mapping(target = "space", ignore = true),
            @Mapping(target = "teamMembershipId", ignore = true),
            @Mapping(target = "wishlist", ignore = true),
            @Mapping(target = "id", ignore = true),
    })
    Person toPerson(TeamMembership teamMembership);
}
