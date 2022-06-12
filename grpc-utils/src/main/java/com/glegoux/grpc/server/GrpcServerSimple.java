package com.glegoux.grpc.server;

import io.grpc.*;
import io.grpc.health.v1.HealthGrpc;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.protobuf.services.HealthStatusManager;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.grpc.reflection.v1alpha.ServerReflectionGrpc;
import io.prometheus.client.exporter.HTTPServer;
import me.dinowernli.grpc.prometheus.Configuration;
import me.dinowernli.grpc.prometheus.MonitoringServerInterceptor;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class GrpcServerSimple implements GrpcServer {

    private static final Logger LOGGER = Logger.getLogger(GrpcServerSimple.class.getName());

    private Server server;
    private HTTPServer monitoringServer;

    private final int port;
    private final int monitoringPort;
    private final int numberOfThreads;
    private final List<BindableService> services;


    public GrpcServerSimple(int port, int monitoringPort, int numberOfThreads, List<BindableService> services) {
        this.port = port;
        this.monitoringPort = monitoringPort;
        this.services = services;
        this.numberOfThreads = numberOfThreads;
    }

    @Override
    public void start() throws IOException {

        MonitoringServerInterceptor monitoringInterceptor = MonitoringServerInterceptor.create(Configuration.allMetrics());

        ThreadPoolExecutor healthServiceThreadPoolExecutor = GrpcThreadPoolHelper.newFixedThreadPool(1, "grpc-health-service");
        ThreadPoolExecutor reflectionServiceThreadPoolExecutor = GrpcThreadPoolHelper.newFixedThreadPool(1, "reflection-service");
        ThreadPoolExecutor servicesThreadPoolExecutor = GrpcThreadPoolHelper.newFixedThreadPool(this.numberOfThreads, "grpc-services");

        NettyServerBuilder serverBuilder = NettyServerBuilder.forPort(this.port)
                .callExecutor(new ServerCallExecutorSupplier() {
                    @Override
                    public <ReqT, RespT> Executor getExecutor(ServerCall<ReqT, RespT> call, Metadata metadata) {
                        String serviceName = call.getMethodDescriptor().getServiceName();
                        if (serviceName != null) {
                            if (serviceName.equals(HealthGrpc.SERVICE_NAME)) {
                                return healthServiceThreadPoolExecutor;
                            } else if (serviceName.equals(ServerReflectionGrpc.SERVICE_NAME)) {
                                return reflectionServiceThreadPoolExecutor;
                            }
                        }
                        return servicesThreadPoolExecutor;
                    }
                })
                .intercept(monitoringInterceptor)
                .addService(new HealthStatusManager().getHealthService())
                .addService(ProtoReflectionService.newInstance());

        this.services.forEach(serverBuilder::addService);

        this.server = serverBuilder.build().start();

        LOGGER.info("gRPC server started, listening on " + this.port);

        this.monitoringServer = new HTTPServer(this.monitoringPort);

        LOGGER.info("Monitoring server for gRPC metrics started, listening on " + this.monitoringPort);

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

    @Override
    public void stop() throws InterruptedException {
        if (this.server != null) {
            this.server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
        if (this.monitoringServer != null) {
            this.monitoringServer.close();
        }
    }

    @Override
    public void blockUntilShutdown() throws InterruptedException {
        if (this.server != null) {
            this.server.awaitTermination();
        }
    }

    public static GrpcServerSimple run(String programName, String[] args, List<BindableService> services) throws IOException {
        GrpcServerSimpleArguments arguments = new GrpcServerSimpleArguments(programName, args);
        int port = arguments.getPort();
        int monitoringPort = arguments.getMonitoringPort();
        int numberOfThreads = arguments.getThread();
        GrpcServerSimple grpcServerSimple = new GrpcServerSimple(port, monitoringPort, numberOfThreads, services);
        grpcServerSimple.start();
        return grpcServerSimple;
    }

    public int getPort() {
        return port;
    }

    public int getMonitoringPort() {
        return monitoringPort;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }
}