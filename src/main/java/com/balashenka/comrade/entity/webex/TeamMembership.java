package com.balashenka.comrade.entity.webex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamMembership {
    @JsonProperty("id")
    private String id;
    @JsonProperty("teamId")
    private String teamId;
    @JsonProperty("personId")
    private String personId;
    @JsonProperty("personDisplayName")
    private String personDisplayName;
    @JsonProperty("personEmail")
    private String personEmail;
    @JsonProperty("isModerator")
    private Boolean isModerator;
    @JsonProperty("created")
    private String created;
}
