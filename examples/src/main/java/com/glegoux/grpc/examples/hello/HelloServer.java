package com.glegoux.grpc.examples.hello;

import com.glegoux.examples.hello.HelloGrpc;
import com.glegoux.examples.hello.HelloReply;
import com.glegoux.examples.hello.HelloRequest;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.HealthStatusManager;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.grpc.stub.StreamObserver;
import io.prometheus.client.exporter.HTTPServer;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import me.dinowernli.grpc.prometheus.Configuration;
import me.dinowernli.grpc.prometheus.MonitoringServerInterceptor;

public class HelloServer {
  private static final Logger logger = Logger.getLogger(HelloServer.class.getName());

  private Server server;
  private HTTPServer monitoringServer;

  public static void main(String[] args) throws IOException, InterruptedException {
    HelloArguments arguments = new HelloArguments(args);
    short port = arguments.getPort();
    short monitoringPort = arguments.getMonitoringPort();
    final HelloServer server = new HelloServer();
    server.start(port, monitoringPort);
    server.blockUntilShutdown();
  }

  private void start(int port, int monitoringPort) throws IOException {

    MonitoringServerInterceptor monitoringInterceptor =
        MonitoringServerInterceptor.create(Configuration.allMetrics());

    server = ServerBuilder.forPort(port)
        .intercept(monitoringInterceptor)
        .addService(new HealthStatusManager().getHealthService())
        .addService(ProtoReflectionService.newInstance())
        .addService(new HelloImpl())
        .build()
        .start();

    logger.info("gRPC server started, listening on " + port);

    monitoringServer = new HTTPServer(monitoringPort);

    logger.info("Monitoring server for gRPC metrics started, listening on " + monitoringPort);

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        System.err.println("shutting down gRPC server since JVM is shutting down");
        try {
          HelloServer.this.stop();
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
        }
        System.err.println("gRPC server shut down");
      }
    });

  }

  private void stop() throws InterruptedException {
    if (server != null) {
      server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
    if (monitoringServer != null) {
      monitoringServer.close();
    }
  }

  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

  private static class HelloImpl extends HelloGrpc.HelloImplBase {

    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
      HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + req.getName()).build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }
  }

}
