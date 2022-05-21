package com.glegoux.grpc.server;

import io.prometheus.client.CollectorRegistry;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

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
        // GIVEN
        String[] args = new String[]{};

        //WHEN
        server = GrpcServerSimple.run("test", args, new ArrayList<>());

        //THEN
        Assertions.assertThat(server.getPort()).isEqualTo(8000);
        Assertions.assertThat(server.getMonitoringPort()).isEqualTo(8001);
    }


    @Test
    public void test() throws IOException {
        // GIVEN
        String[] args = new String[]{"--port", "9090", "--monitoring-port", "9091"};

        //WHEN
        server = GrpcServerSimple.run("test", args, new ArrayList<>());

        //THEN
        Assertions.assertThat(server.getPort()).isEqualTo(9090);
        Assertions.assertThat(server.getMonitoringPort()).isEqualTo(9091);
    }

}
