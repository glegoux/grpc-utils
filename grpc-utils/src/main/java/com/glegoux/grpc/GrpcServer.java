package com.glegoux.grpc;

import java.io.IOException;

public interface GrpcServer {
    void start()  throws IOException;
    void stop() throws InterruptedException;
    void blockUntilShutdown() throws InterruptedException;
}
