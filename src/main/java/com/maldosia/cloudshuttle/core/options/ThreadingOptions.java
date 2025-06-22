package com.maldosia.cloudshuttle.core.options;

import com.maldosia.cloudshuttle.core.Option;

/**
 * 线程池配置
 * 
 * @author Maldosia
 * @since 2025/6/22
 */
public class ThreadingOptions {
    public static final Option<Integer> WORKER_THREADS =
            new Option.Builder<>("workerThreads", Integer.class)
                    .defaultValue(Runtime.getRuntime().availableProcessors() * 2)
                    .validator(value -> {
                        if (value <= 0) {
                            throw new IllegalArgumentException("Worker threads must be positive");
                        }
                    })
                    .build();

    public static final Option<Integer> TASK_QUEUE_SIZE =
            new Option.Builder<>("taskQueueSize", Integer.class)
                    .defaultValue(1024)
                    .validator(value -> {
                        if (value <= 0) {
                            throw new IllegalArgumentException("Task queue size must be positive");
                        }
                    })
                    .build();
}