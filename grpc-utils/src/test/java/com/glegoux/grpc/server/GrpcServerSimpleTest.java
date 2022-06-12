package com.glegoux.grpc.server;

import io.prometheus.client.CollectorRegistry;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static com.glegoux.grpc.server.GrpcServerSimpleArguments.DEFAULT_MONITORING_PORT;
import static com.glegoux.grpc.server.GrpcServerSimpleArguments.DEFAULT_NUMBER_OF_THREADS;
import static com.glegoux.grpc.server.GrpcServerSimpleArguments.DEFAULT_PORT;

public class GrpcServerSimpleTest {

    private GrpcServerSimple server;

    @After
    public void tearDown() throws InterruptedException {
        server.stop();
        // hack, see https://github.com/prometheus/client_java/issues/279
        CollectorRegistry.defaultRegistry.clear();
    }

    @Test
    public void testDefault() throws IOException {
        String[] args = new String[]{};

        server = GrpcServerSimple.run("test", args, new ArrayList<>());

        Assertions.assertThat(server.getPort()).isEqualTo(DEFAULT_PORT);
        Assertions.assertThat(server.getMonitoringPort()).isEqualTo(DEFAULT_MONITORING_PORT);
        Assertions.assertThat(server.getNumberOfThreads()).isEqualTo(DEFAULT_NUMBER_OF_THREADS);
    }


    @Test
    public void testPortAndMonitoringPort() throws IOException {
        String[] args = new String[]{"--port", "9090", "--monitoring-port", "9091"};

        server = GrpcServerSimple.run("test", args, new ArrayList<>());

        Assertions.assertThat(server.getPort()).isEqualTo(9090);
        Assertions.assertThat(server.getMonitoringPort()).isEqualTo(9091);
        Assertions.assertThat(server.getNumberOfThreads()).isEqualTo(DEFAULT_NUMBER_OF_THREADS);
    }

    @Test
    public void testNumberOfThreads() throws IOException {
        String[] args = new String[]{"--number-threads", "4"};

        server = GrpcServerSimple.run("test", args, new ArrayList<>());

        Assertions.assertThat(server.getPort()).isEqualTo(DEFAULT_PORT);
        Assertions.assertThat(server.getMonitoringPort()).isEqualTo(DEFAULT_MONITORING_PORT);
        Assertions.assertThat(server.getNumberOfThreads()).isEqualTo(4);
    }
}
