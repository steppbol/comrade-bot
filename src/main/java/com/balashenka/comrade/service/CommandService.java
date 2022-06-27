package com.balashenka.comrade.service;

public interface CommandService {
    void handle(String personEmail, String messageId);
}
