package com.glegoux.grpc.examples.hello;

import com.glegoux.grpc.gRPCServer;
import com.glegoux.grpc.gRPCServerArguments;

import java.io.IOException;

public class HelloServer {

  public static void main(String[] args) throws IOException, InterruptedException {
    gRPCServerArguments arguments = new gRPCServerArguments("hello", args);
    short port = arguments.getPort();
    short monitoringPort = arguments.getMonitoringPort();
    final gRPCServer server = new gRPCServer();
    server.start(port, monitoringPort);
    server.blockUntilShutdown();
  }

}
