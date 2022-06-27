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
public class Room {
    @JsonProperty("id")
    private String id;
    @JsonProperty("title")
    private String title;
    @JsonProperty("teamId")
    private String teamId;
    @JsonProperty("isLocked")
    private Boolean isLocked;
    @JsonProperty("created")
    private String created;
    @JsonProperty("lastActivity")
    private String lastActivity;
    @JsonProperty("type")
    private String type;
    @JsonProperty("sipAddress")
    private String sipAddress;
}
