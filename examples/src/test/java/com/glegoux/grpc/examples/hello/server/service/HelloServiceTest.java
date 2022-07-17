package com.glegoux.grpc.examples.hello.server.service;

import com.glegoux.examples.hello.HelloReply;
import com.glegoux.examples.hello.HelloRequest;
import com.glegoux.examples.hello.HelloServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class HelloServiceTest {

    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    private HelloServiceGrpc.HelloServiceBlockingStub client;

    @Before
    public void setUp() throws Exception {
        String serverName = InProcessServerBuilder.generateName();

        grpcCleanup.register(
                InProcessServerBuilder
                        .forName(serverName)
                        .directExecutor()
                        .addService(new HelloService())
                        .build().start()
        );

        ManagedChannel channel = grpcCleanup.register(
                InProcessChannelBuilder
                        .forName(serverName)
                        .directExecutor()
                        .build()
        );

        client = HelloServiceGrpc.newBlockingStub(channel);
    }

    @Test
    public void testSayHello() {
        HelloRequest req = HelloRequest.newBuilder().setName("world!").build();
        HelloReply rep = this.client.sayHello(req);
        Assertions.assertThat(rep.getMessage()).isEqualTo("Hello world!");
    }

    @Test
    public void testHello() {
        Assertions.assertThat(HelloService.hello("world!")).isEqualTo("Hello world!");
    }
}
