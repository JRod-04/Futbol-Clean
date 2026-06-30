package com.futbol.estadisticas;

import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.dockerclient.DockerClientProviderStrategy;
import org.testcontainers.dockerclient.NpipeSocketClientProviderStrategy;

import java.lang.reflect.Field;
import java.net.URI;
import java.time.Duration;

public abstract class PostgresTestContainerConfig {

    protected static final PostgreSQLContainer<?> POSTGRES;

    static {
        try {
            // 1. Construir el cliente docker-java con el pipe que sabemos funciona
            var config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                    .withDockerHost("npipe:////./pipe/docker_engine")
                    .build();

            var httpClient = new ApacheDockerHttpClient.Builder()
                    .dockerHost(new URI("npipe:////./pipe/docker_engine"))
                    .maxConnections(5)
                    .connectionTimeout(Duration.ofSeconds(10))
                    .responseTimeout(Duration.ofSeconds(30))
                    .build();

            var dockerClient = DockerClientImpl.getInstance(config, httpClient);

            // 2. Inyectar el cliente directamente en el campo "client" de la factory
            DockerClientFactory factory = DockerClientFactory.instance();

            Field clientField = DockerClientFactory.class.getDeclaredField("client");
            clientField.setAccessible(true);
            clientField.set(factory, dockerClient);

            // 3. Inyectar una estrategia dummy en "strategy" para que no intente detectar Docker
            Field strategyField = DockerClientFactory.class.getDeclaredField("strategy");
            strategyField.setAccessible(true);
            strategyField.set(factory, new NpipeSocketClientProviderStrategy());

            // 4. Limpiar cachedClientFailure para que no bloquee el arranque
            Field failureField = DockerClientFactory.class.getDeclaredField("cachedClientFailure");
            failureField.setAccessible(true);
            failureField.set(factory, null);

            System.out.println("Docker client inyectado correctamente");

        } catch (Exception e) {
            throw new RuntimeException("Error inyectando Docker client: " + e.getMessage(), e);
        }

        POSTGRES = new PostgreSQLContainer<>("postgres:15-alpine")
                .withDatabaseName("estadisticas_test")
                .withUsername("test_user")
                .withPassword("test_pass");

        POSTGRES.start();
    }
}