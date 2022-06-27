package com.balashenka.comrade.client;

import java.util.List;
import java.util.Map;

public interface WebexApiClient {
    <T> T create(T entity, String path, Class<T> type);

    <T> T get(String id, String path, Map<String, String> queryParameters, Class<T> type);

    <T> List<T> getAll(String path, Map<String, String> queryParameters, Class<T> type);

    boolean delete(String id, String path);
}
