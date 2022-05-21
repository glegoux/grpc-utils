package com.glegoux.grpc.examples.hello.service;

import com.glegoux.examples.hello.HelloReply;
import com.glegoux.examples.hello.HelloRequest;
import io.grpc.stub.StreamObserver;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class HelloServiceTest {
    private HelloReply valueOnNext;
    private HelloReply valueOnCompleted;
    StreamObserver< HelloReply > responseObserver = new StreamObserver<HelloReply>() {

        @Override
        public void onNext(HelloReply value) {
            valueOnNext = value;
        }

        @Override
        public void onError(Throwable t) {
            throw new RuntimeException();
        }

        @Override
        public void onCompleted() {
            valueOnCompleted = valueOnNext;
        }
    };

    @Test
    public void test() {
        HelloService helloService = new HelloService();
        HelloRequest req = HelloRequest.newBuilder().setName("world!").build();

        helloService.sayHello(req, responseObserver);

        Assertions.assertThat(valueOnNext.getMessage()).isEqualTo("Hello world!");
        Assertions.assertThat(valueOnCompleted.getMessage()).isEqualTo("Hello world!");
    }
}
