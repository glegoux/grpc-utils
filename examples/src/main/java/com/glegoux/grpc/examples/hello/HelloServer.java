package com.glegoux.grpc.examples.hello;

import com.glegoux.examples.hello.HelloGrpc;
import com.glegoux.examples.hello.HelloReply;
import com.glegoux.examples.hello.HelloRequest;
import com.glegoux.grpc.GrpcServer;
import com.glegoux.grpc.GrpcServerRunner;
import com.glegoux.grpc.GrpcServerSimple;
import com.google.common.collect.Lists;
import io.grpc.BindableService;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.List;

public class HelloServer {

  public static void main(String[] args) throws IOException, InterruptedException {
    List<BindableService> services = Lists.newArrayList(new HelloImpl());
    GrpcServer grpcServer = new GrpcServerSimple(services);
    GrpcServerRunner.run("hello", args, grpcServer);
  }

  private static class HelloImpl extends HelloGrpc.HelloImplBase {

    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
      HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + req.getName()).build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }
  }

}
