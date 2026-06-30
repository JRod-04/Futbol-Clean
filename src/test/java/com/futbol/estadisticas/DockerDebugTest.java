package com.futbol.estadisticas;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Duration;

public class DockerDebugTest {

    @Test
    void debugDocker() throws Exception {
        System.out.println("🔍 DIAGNÓSTICO DIRECTO SIN TESTCONTAINERS");
        System.out.println("==========================================");

        // Probar los tres pipes directamente con docker-java, sin pasar por Testcontainers
        String[] pipes = {
            "npipe:////./pipe/docker_cli",
            "npipe:////./pipe/docker_engine",
            "npipe:////./pipe/dockerDesktopLinuxEngine"
        };

        for (String pipe : pipes) {
            System.out.println("\n▶ Probando: " + pipe);
            try {
                var config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                        .withDockerHost(pipe)
                        .build();

                var httpClient = new ApacheDockerHttpClient.Builder()
                        .dockerHost(new URI(pipe))
                        .maxConnections(1)
                        .connectionTimeout(Duration.ofSeconds(3))
                        .responseTimeout(Duration.ofSeconds(5))
                        .build();

                DockerClient client = DockerClientImpl.getInstance(config, httpClient);
                var info = client.infoCmd().exec();

                System.out.println("  ✅ CONECTADO");
                System.out.println("  OS: "      + info.getOperatingSystem());
                System.out.println("  Versión: " + info.getServerVersion());
                System.out.println("  Nombre: "  + info.getName());
                client.close();

            } catch (Exception e) {
                System.out.println("  ❌ " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    }
}