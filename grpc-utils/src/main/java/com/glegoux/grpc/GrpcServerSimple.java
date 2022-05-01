package com.glegoux.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.HealthStatusManager;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.prometheus.client.exporter.HTTPServer;
import me.dinowernli.grpc.prometheus.Configuration;
import me.dinowernli.grpc.prometheus.MonitoringServerInterceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class GrpcServerSimple implements GrpcServer {

    private static final Logger LOGGER = Logger.getLogger(GrpcServerSimple.class.getName());

    private Server server;
    private HTTPServer monitoringServer;

    public void start(int port, int monitoringPort) throws IOException {

        MonitoringServerInterceptor monitoringInterceptor = MonitoringServerInterceptor.create(Configuration.allMetrics());

        server = ServerBuilder.forPort(port)
            .intercept(monitoringInterceptor)
            .addService(new HealthStatusManager().getHealthService())
            .addService(ProtoReflectionService.newInstance())
            .build()
            .start();

        LOGGER.info("gRPC server started, listening on " + port);

        monitoringServer = new HTTPServer(monitoringPort);

        LOGGER.info("Monitoring server for gRPC metrics started, listening on " + monitoringPort);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Use stderr here since the logger may have been reset by its JVM shutdown hook.
            System.err.println("Shutting down gRPC server since JVM is shutting down");
            try {
                stop();
            } catch (InterruptedException e) {
                System.err.printf("Server interrupted %s%n", e);
            }
            System.err.println("gRPC server shut down");
        }));
    }

    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
        if (monitoringServer != null) {
            monitoringServer.close();
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public void run(String programName, String[] args) throws IOException, InterruptedException {
        GrpcServerSimpleArguments arguments = new GrpcServerSimpleArguments(programName, args);
        int port = arguments.getPort();
        int monitoringPort = arguments.getMonitoringPort();
        final GrpcServerSimple server = new GrpcServerSimple();
        server.start(port, monitoringPort);
        server.blockUntilShutdown();
    }

}