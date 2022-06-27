package com.balashenka.comrade.controller;

import com.balashenka.comrade.exception.Message;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.persistence.EntityNotFoundException;

@Log4j2
@RestControllerAdvice
public final class ControllerAdvice {
    @ExceptionHandler(value = {RuntimeException.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public String runtimeException(@NonNull RuntimeException exception, @NonNull WebRequest request) {
        log.error("ERROR: {}; session ID: {}", exception.getMessage(), request.getSessionId());
        exception.printStackTrace();
        return exception.getMessage();
    }

    @ExceptionHandler(value = {EntityNotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public String runtimeException(@NonNull EntityNotFoundException exception, @NonNull WebRequest request) {
        log.error("ERROR: {}; session ID: {}", exception.getMessage(), request.getSessionId());
        exception.printStackTrace();
        return Message.ENTITY_NOT_FOUND;
    }
}
