package com.glegoux.grpc.examples.hello.service;

import com.glegoux.examples.hello.HelloReply;
import com.glegoux.examples.hello.HelloRequest;
import io.grpc.stub.StreamObserver;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class HelloServiceTest {

    AtomicInteger counter = new AtomicInteger();
    AtomicInteger counter2 = new AtomicInteger();

    StreamObserver< HelloReply > responseObserver = new StreamObserver<HelloReply>() {
        @Override
        public void onNext(HelloReply value) {
            counter.incrementAndGet();
        }

        @Override
        public void onError(Throwable t) {
            throw new RuntimeException();
        }

        @Override
        public void onCompleted() {
            counter2.incrementAndGet();
        }
    };

    @Test
    public void test() {
        //GIVEN
        HelloService helloService = new HelloService();
        HelloRequest req = HelloRequest.newBuilder().build();

        //WHEN
        helloService.sayHello(req, responseObserver);

        //THEN
        Assertions.assertThat(counter.get()).isEqualTo(1);
        Assertions.assertThat(counter2.get()).isEqualTo(1);
    }
}
