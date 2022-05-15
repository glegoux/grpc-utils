package com.glegoux.grpc.examples.hello;

import com.glegoux.grpc.GrpcServerSimple;
import com.google.common.collect.Lists;
import io.grpc.BindableService;

import java.io.IOException;
import java.util.List;

public class HelloServer {

  public static void main(String[] args) throws IOException, InterruptedException {
    List<BindableService> services = Lists.newArrayList(new HelloService());
    GrpcServerSimple.run("hello", args, services);
  }

}
