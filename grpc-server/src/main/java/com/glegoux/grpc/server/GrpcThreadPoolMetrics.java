package com.glegoux.grpc.server;

import io.prometheus.client.Gauge;

import java.util.concurrent.ThreadPoolExecutor;

public class GrpcThreadPoolMetrics {

    public static void build(ThreadPoolExecutor threadPool, String threadPoolName) {
        POOL_SIZE_GAUGE.setChild(new Gauge.Child() {
            @Override
            public double get() {
                return threadPool.getPoolSize();
            }
        }, threadPoolName);
        ACTIVE_TASK_GAUGE.setChild(new Gauge.Child() {
            @Override
            public double get() {
                return threadPool.getActiveCount();
            }
        }, threadPoolName);
        SCHEDULED_TASK_GAUGE.setChild(new Gauge.Child() {
            @Override
            public double get() {
                return threadPool.getTaskCount();
            }
        }, threadPoolName);
        AWAITING_TASK_GAUGE.setChild(new Gauge.Child() {
            @Override
            public double get() {
                return threadPool.getQueue().size();
            }
        }, threadPoolName);
    }

    private static final Gauge POOL_SIZE_GAUGE = Gauge.build()
            .namespace("grpc")
            .name("threadpool_pool_size")
            .labelNames("threadpool_name")
            .help("Total number of thread(s) in the thread pool")
            .create()
            .register();

    private static final Gauge ACTIVE_TASK_GAUGE = Gauge.build()
            .namespace("grpc")
            .name("threadpool_active_task")
            .labelNames("threadpool_name")
            .help("Total number of current active thread(s) in the thread pool")
            .create()
            .register();

    private static final Gauge SCHEDULED_TASK_GAUGE = Gauge.build()
            .namespace("grpc")
            .name("threadpool_scheduled_task")
            .labelNames("threadpool_name")
            .help("Total number of scheduled task(s) by the thread pool")
            .create()
            .register();

    private static final Gauge AWAITING_TASK_GAUGE = Gauge.build()
            .namespace("grpc")
            .name("threadpool_awaiting_task")
            .labelNames("threadpool_name")
            .help("Total number of awaiting tasks in the thread pool")
            .create()
            .register();

}
