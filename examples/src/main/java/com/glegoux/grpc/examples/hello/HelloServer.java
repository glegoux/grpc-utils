package com.glegoux.grpc.examples.hello;

import com.glegoux.grpc.GrpcServerSimple;

import java.io.IOException;

public class HelloServer {

  public static void main(String[] args) throws IOException, InterruptedException {
    new GrpcServerSimple().run("hello", args);
  }

}
