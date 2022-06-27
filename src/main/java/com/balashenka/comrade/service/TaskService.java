package com.balashenka.comrade.service;

import com.balashenka.comrade.util.task.CancelableTask;

import java.time.Duration;
import java.util.UUID;

public interface TaskService {
    <T> void run(CancelableTask<T> task, Duration duration);

    boolean cancel(UUID id);
}
