package com.glegoux.grpc.examples.hello;

import com.glegoux.examples.hello.HelloGrpc;
import com.glegoux.examples.hello.HelloReply;
import com.glegoux.examples.hello.HelloRequest;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class HelloServer {
  private static final Logger logger = Logger.getLogger(HelloServer.class.getName());

  private Server server;

  public static void main(String[] args) throws IOException, InterruptedException {
    final HelloServer server = new HelloServer();
    server.start();
    server.blockUntilShutdown();
  }

  private void start() throws IOException {
    int port = 8080;

    server = ServerBuilder.forPort(port)
        .addService(new HelloImpl())
        .addService(ProtoReflectionService.newInstance())
        .build()
        .start();

    logger.info("Server started, listening on " + port);

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        try {
          HelloServer.this.stop();
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
        }
        System.err.println("grpc server shut down");
      }
    });

  }

  private void stop() throws InterruptedException {
    if (server != null) {
      server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
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
