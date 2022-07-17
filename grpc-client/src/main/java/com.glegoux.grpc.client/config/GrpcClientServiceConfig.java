package com.glegoux.grpc.client.config;

import io.grpc.internal.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class GrpcClientServiceConfig {

    private static final String DEFAULT_CONFIG_NAME = "grpc_client_service_config.json";

    public static Map<String, ?> build() {
        return build(DEFAULT_CONFIG_NAME);
    }

    public static Map<String, ?> build(String configName) {
        try (InputStream is = GrpcClientServiceConfig.class.getClassLoader().getResourceAsStream(configName)) {
            if (is == null) {
                throw new IllegalArgumentException(String.format("Failed to read gRPC client config for services named %s, the resource does not exist", configName));
            }
            try (InputStreamReader isr = new InputStreamReader(is, UTF_8);
                 BufferedReader br = new BufferedReader(isr)) {
                String jsonString = br.lines().collect(Collectors.joining("\n"));
                return (Map<String, ?>) JsonParser.parse(jsonString);
            }
        } catch (IOException e) {
            throw new RuntimeException(String.format("Failed to parse gRPC client config for services named %s", configName));
        }
    }

}
