package com.balashenka.comrade.entity.webex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttachmentAction {
    @JsonProperty("id")
    private String id;
    @JsonProperty("personId")
    private String personId;
    @JsonProperty("roomId")
    private String roomId;
    @JsonProperty("type")
    private String type;
    @JsonProperty("created")
    private String created;
    @JsonProperty("messageId")
    private String messageId;
    @JsonProperty("inputs")
    private Map<String, String> inputs;
}
