package com.glegoux.grpc;

import java.io.IOException;

public interface GrpcServer {
    void start(int port, int monitoringPort)  throws IOException;
    void stop() throws InterruptedException;
    void blockUntilShutdown() throws InterruptedException;
}
