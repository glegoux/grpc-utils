package com.glegoux.grpc.examples.hello.server.service;

import com.glegoux.examples.hello.HelloReply;
import com.glegoux.examples.hello.HelloRequest;
import com.glegoux.examples.hello.HelloServiceGrpc;
import io.grpc.stub.StreamObserver;

public class HelloService extends HelloServiceGrpc.HelloServiceImplBase {

    //private static final Logger LOGGER = Logger.getLogger(HelloService.class.getName());

    //private final AtomicInteger totalRpcs = new AtomicInteger();

    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
        HelloReply reply = HelloReply.newBuilder().setMessage(hello(req.getName())).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
        //int numberOfRpcs = totalRpcs.incrementAndGet();
        //LOGGER.info(String.format("Number of RPCs: %d", numberOfRpcs));
        //try {
        //    Thread.sleep(10 * 1000);
        //} catch (InterruptedException e) {}
        //responseObserver.onError(new StatusRuntimeException(Status.UNAVAILABLE));
    }

    static String hello(String name) {
        return "Hello " + name;
    }

}
