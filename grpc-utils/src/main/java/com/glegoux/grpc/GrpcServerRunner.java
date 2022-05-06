package com.glegoux.grpc;

import java.io.IOException;

public class GrpcServerRunner {

    public static void run(String programName, String[] args, GrpcServer grpcServer) throws IOException, InterruptedException {
        GrpcServerSimpleArguments arguments = new GrpcServerSimpleArguments(programName, args);
        int port = arguments.getPort();
        int monitoringPort = arguments.getMonitoringPort();
        grpcServer.start(port, monitoringPort);
        grpcServer.blockUntilShutdown();
    }

}
