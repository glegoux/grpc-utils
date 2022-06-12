package com.glegoux.grpc.examples.hello.service;

import com.glegoux.examples.hello.HelloReply;
import com.glegoux.examples.hello.HelloRequest;
import com.glegoux.examples.hello.HelloServiceGrpc;
import io.grpc.stub.StreamObserver;

public class HelloService extends HelloServiceGrpc.HelloServiceImplBase {

    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
        HelloReply reply = HelloReply.newBuilder().setMessage(hello(req.getName())).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    static String hello(String name) {
        return "Hello " + name;
    }

}
