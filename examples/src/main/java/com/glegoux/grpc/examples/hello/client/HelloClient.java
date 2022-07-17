package com.glegoux.grpc.examples.hello.client;

import com.glegoux.examples.hello.HelloReply;
import com.glegoux.examples.hello.HelloRequest;
import com.glegoux.examples.hello.HelloServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;

import java.util.HashMap;
import java.util.Map;

public class HelloClient {

    public static void main(String[] args) {
        NettyChannelBuilder channelBuilder = NettyChannelBuilder.forAddress("localhost", 8000);
        Map<String, ?> serviceConfig = new HashMap<>(); //;GrpcClientServiceConfig.build();
        channelBuilder.defaultServiceConfig(serviceConfig).defaultLoadBalancingPolicy("").usePlaintext();
        ManagedChannel channel = channelBuilder.build();
        HelloServiceGrpc.HelloServiceBlockingStub blockingClient = HelloServiceGrpc.newBlockingStub(channel);

        HelloRequest request = HelloRequest.newBuilder().setName("test").build();
        HelloReply reply = blockingClient.sayHello(request);
    }

}
