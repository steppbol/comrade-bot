package com.balashenka.comrade.util.task;

import java.util.UUID;

public interface CancelableTask<T> {
    T run();

    boolean predicate(T result);

    boolean cancel(UUID taskId);
}
