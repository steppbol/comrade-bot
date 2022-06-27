package com.balashenka.comrade.dto;

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
public class WebhookEventDto {
    @JsonProperty("data")
    private Data data;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {
        @JsonProperty("id")
        private String id;
        @JsonProperty("roomId")
        private String roomId;
        @JsonProperty("personId")
        private String personId;
        @JsonProperty("personEmail")
        private String personEmail;
        @JsonProperty("created")
        private String created;
    }
}
