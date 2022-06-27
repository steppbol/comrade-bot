package com.balashenka.comrade.client.impl;

import com.balashenka.comrade.client.WebexApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.balashenka.comrade.configuration.ComradeProperty;
import com.balashenka.comrade.entity.webex.EntityHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class DefaultApiClient implements WebexApiClient {
    private final RestTemplate restTemplate;
    private final ComradeProperty comradeProperty;
    private final ObjectMapper objectMapper;

    @Autowired
    public DefaultApiClient(RestTemplate restTemplate, ComradeProperty comradeProperty, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.comradeProperty = comradeProperty;
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> T create(T entity, String path, Class<T> type) {
        var finalUrl = comradeProperty.getCisco().getApi() + path;

        var response = restTemplate.exchange(finalUrl, HttpMethod.POST,
                new HttpEntity<>(entity, getHttpHeader()), type);

        T created = null;
        if (response.getStatusCode().equals(HttpStatus.OK) || response.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
            created = response.getBody();
        }

        return created;
    }

    @Override
    public <T> T get(String id, String path, Map<String, String> queryParameters, Class<T> type) {
        var finalUrl = UriComponentsBuilder.fromHttpUrl(comradeProperty.getCisco().getApi() + path);

        if (id != null && !id.isBlank()) {
            finalUrl.path("/");
            finalUrl.path(id);
        }

        if (queryParameters != null && queryParameters.size() > 0) {
            queryParameters.forEach(finalUrl::queryParam);
        }

        var response = restTemplate.exchange(finalUrl.encode().toUriString(), HttpMethod.GET,
                new HttpEntity<>("", getHttpHeader()), type);

        T found = null;
        if (response.getStatusCode().equals(HttpStatus.OK) || response.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
            found = response.getBody();
        }

        return found;
    }

    @Override
    public <T> List<T> getAll(String path, Map<String, String> queryParameters, Class<T> type) {
        var finalUrl = UriComponentsBuilder.fromHttpUrl(comradeProperty.getCisco().getApi() + path);

        if (queryParameters != null && queryParameters.size() > 0) {
            queryParameters.forEach(finalUrl::queryParam);
        }

        var response = restTemplate.exchange(finalUrl.encode().toUriString(), HttpMethod.GET,
                new HttpEntity<>("", getHttpHeader()), EntityHolder.class);

        EntityHolder<T> found = new EntityHolder<>();
        found.setItems(new LinkedList<>());
        if (response.getStatusCode().equals(HttpStatus.OK) || response.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
            var body = response.getBody();
            if (body != null) {
                for (var item : body.getItems()) {
                    found.getItems().add(objectMapper.convertValue(item, type));
                }
            }
        }

        return found.getItems();
    }

    @Override
    public boolean delete(String id, String path) {
        var finalUrl = comradeProperty.getCisco().getApi() + path + "/" + URLEncoder.encode(id, StandardCharsets.UTF_8);

        var response = restTemplate.exchange(finalUrl, HttpMethod.DELETE,
                new HttpEntity<>("", getHttpHeader()), Void.class);

        return response.getStatusCode().equals(HttpStatus.OK) || response.getStatusCode().equals(HttpStatus.NO_CONTENT);
    }

    @NonNull
    private HttpHeaders getHttpHeader() {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(comradeProperty.getCisco().getToken());
        return headers;
    }
}
