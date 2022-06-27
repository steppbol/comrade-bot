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
public class Webhook {
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("resource")
    private String resource;
    @JsonProperty("event")
    private String event;
    @JsonProperty("filter")
    private String filter;
    @JsonProperty("targetUrl")
    private String targetUrl;
    @JsonProperty("secret")
    private String secret;
    @JsonProperty("created")
    private String created;
}
