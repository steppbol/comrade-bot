package com.balashenka.comrade.entity.webex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {
    @JsonProperty("id")
    private String id;
    @JsonProperty("roomId")
    private String roomId;
    @JsonProperty("markdown")
    private String markdown;
    @JsonProperty("attachments")
    private List<Map<String, ?>> attachments;
    @JsonProperty("toPersonId")
    private String toPersonId;
    @JsonProperty("toPersonEmail")
    private String toPersonEmail;
    @JsonProperty("personId")
    private String personId;
    @JsonProperty("personEmail")
    private String personEmail;
    @JsonProperty("text")
    private String text;
    @JsonProperty("file")
    private String file;
    @JsonProperty("roomType")
    private String roomType;
    @JsonProperty("created")
    private String created;
    @JsonProperty("files")
    private List<String> files;
    @JsonProperty("html")
    private String html;
    @JsonProperty("mentionedPeople")
    private List<String> mentionedPeople;
}
