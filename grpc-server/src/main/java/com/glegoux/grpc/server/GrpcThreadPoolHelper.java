package com.glegoux.grpc.server;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public class GrpcThreadPoolHelper {

    public static ThreadPoolExecutor newFixedThreadPool(int nThreads, String threadPoolName) {
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads, new ThreadFactory() {
            final AtomicInteger counter = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable runnable) {
                String threadName = String.format("grpc-fixed-threadpool-%s-%d_%d", threadPoolName, nThreads, counter.incrementAndGet());
                Thread thread = new Thread(runnable, threadName);
                thread.setDaemon(true);
                return thread;
            }
        });
        GrpcThreadPoolMetrics.build(threadPool, threadPoolName);
        return threadPool;
    }

}
