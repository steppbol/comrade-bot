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
public class Membership {
    @JsonProperty("id")
    private String id;
    @JsonProperty("roomId")
    private String roomId;
    @JsonProperty("personEmail")
    private String personEmail;
    @JsonProperty("personId")
    private String personId;
    @JsonProperty("personDisplayName")
    private String personDisplayName;
    @JsonProperty("isModerator")
    private Boolean isModerator;
    @JsonProperty("isMonitor")
    private Boolean isMonitor;
    @JsonProperty("created")
    private String created;
}
