package com.glegoux.grpc.server;

import io.prometheus.client.Gauge;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GrpcThreadPoolMetrics {

    public static void build(ThreadPoolExecutor threadPool, String threadPoolName) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
                    poolSizeGauge.labels(threadPoolName).set(threadPool.getPoolSize());
                    activeTaskGauge.labels(threadPoolName).set(threadPool.getActiveCount());
                    scheduledTaskGauge.labels(threadPoolName).set(threadPool.getTaskCount());
                    awaitingTaskGauge.labels(threadPoolName).set(threadPool.getQueue().size());
                },
                0,
                1,
                TimeUnit.SECONDS
        );
    }

    private static final Gauge poolSizeGauge = Gauge.build()
            .namespace("grpc")
            .name("threadpool_pool_size")
            .labelNames("threadpool_name")
            .help("Total number of thread(s) in the thread pool")
            .create()
            .register();
    private static final Gauge activeTaskGauge = Gauge.build()
            .namespace("grpc")
            .name("threadpool_active_task")
            .labelNames("threadpool_name")
            .help("Total number of current active thread(s) in the thread pool")
            .create()
            .register();

    private static final Gauge scheduledTaskGauge = Gauge.build()
            .namespace("grpc")
            .name("threadpool_scheduled_task")
            .labelNames("threadpool_name")
            .help("Total number of scheduled task(s) by the thread pool")
            .create()
            .register();

    private static final Gauge awaitingTaskGauge = Gauge.build()
            .namespace("grpc")
            .name("threadpool_awaiting_task")
            .labelNames("threadpool_name")
            .help("Total number of awaiting tasks in the thread pool")
            .create()
            .register();

}
