package com.balashenka.comrade.service.impl;

import com.balashenka.comrade.service.TaskService;
import com.balashenka.comrade.util.task.CancelableTask;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Log4j2
@Service
public class DefaultTaskService implements TaskService {
    private static final Map<UUID, ScheduledFuture<?>> ID_TO_TASK = new ConcurrentHashMap<>();

    private final TaskScheduler taskScheduler;

    @Autowired
    public DefaultTaskService(@Qualifier("threadPoolTaskScheduler") TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    @Override
    public <T> void run(CancelableTask<T> task, Duration duration) {
        var id = UUID.randomUUID();
        log.info("Run scheduled task. Task ID: {}", id);
        ID_TO_TASK.put(id, taskScheduler.scheduleAtFixedRate(() -> {
            if (task.predicate(task.run())) {
                if (task.cancel(id)) {
                    log.info("Scheduled task end successfully. Task ID: {}", id);
                } else {
                    log.info("Scheduled task end failed. Task ID: {}. Task is not stopped", id);
                    throw new IllegalStateException();
                }
            }
        }, duration));
    }

    @Override
    public boolean cancel(UUID id) {
        var task = ID_TO_TASK.remove(id);
        return task != null && task.cancel(true);
    }
}
