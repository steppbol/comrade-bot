package com.balashenka.comrade.controller;

public abstract class ApiPath {
    public static final String API_PATH = "/api";
    public static final String V1_PATH = "/v1";
    public static final String V2_PATH = "/v2";
    public static final String COMRADE_PATH = "/comrade";
    public static final String GROUPS_PATH = "/groups";
    public static final String MESSAGES_PATH = "/messages";
    public static final String ATTACHMENTS_PATH = "/attachments";
    public static final String MEMBERSHIPS_PATH = "/memberships";

    public static final String API_V1_COMRADE_GROUPS_PATH = API_PATH + V1_PATH + COMRADE_PATH + GROUPS_PATH;
    public static final String API_V2_COMRADE_MESSAGES_PATH = API_PATH + V2_PATH + COMRADE_PATH + MESSAGES_PATH;
    public static final String API_V2_COMRADE_ATTACHMENTS_PATH = API_PATH + V2_PATH + COMRADE_PATH + ATTACHMENTS_PATH;
    public static final String API_V2_COMRADE_MEMBERSHIPS_PATH = API_PATH + V2_PATH + COMRADE_PATH + MEMBERSHIPS_PATH;
}
