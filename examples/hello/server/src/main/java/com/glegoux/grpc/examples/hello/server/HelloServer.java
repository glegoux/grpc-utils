package com.glegoux.grpc.examples.hello.server;

import com.glegoux.grpc.server.GrpcServerSimple;
import com.glegoux.grpc.examples.hello.server.service.HelloService;
import com.google.common.collect.Lists;
import io.grpc.BindableService;

import java.io.IOException;
import java.util.List;

public class HelloServer {

  public static void main(String[] args) throws IOException {
    List<BindableService> services = Lists.newArrayList(new HelloService());
    GrpcServerSimple.run("hello", args, services);
  }

}
