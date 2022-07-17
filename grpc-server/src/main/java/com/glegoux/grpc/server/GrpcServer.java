package com.glegoux.grpc.server;

import java.io.IOException;

public interface GrpcServer {
    void start()  throws IOException;
    void stop() throws InterruptedException;
    void blockUntilShutdown() throws InterruptedException;
}
