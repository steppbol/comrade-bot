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
public class Person {
    @JsonProperty("id")
    private String id;
    @JsonProperty("displayName")
    private String displayName;
    @JsonProperty("emails")
    private List<String> emails;
    @JsonProperty("firstName")
    private String firstName;
    @JsonProperty("lastName")
    private String lastName;
    @JsonProperty("avatar")
    private String avatar;
    @JsonProperty("orgId")
    private String organizationId;
    @JsonProperty("roles")
    private List<String> roles;
    @JsonProperty("licenses")
    private List<String> licenses;
    @JsonProperty("created")
    private String created;
    @JsonProperty("timeZone")
    private String timeZone;
    @JsonProperty("lastActivity")
    private String lastActivity;
    @JsonProperty("status")
    private String status;
    @JsonProperty("type")
    private String type;
    @JsonProperty("phoneNumbers")
    private List<Map<String, String>> phoneNumbers;
    @JsonProperty("loginEnabled")
    private Boolean loginEnabled;
    @JsonProperty("lastModified")
    private String lastModified;
    @JsonProperty("nickName")
    private String nickName;
    @JsonProperty("invitePending")
    private Boolean invitePending;
    @JsonProperty("sipAddresses")
    private List<Map<String, String>> sipAddresses;
}
