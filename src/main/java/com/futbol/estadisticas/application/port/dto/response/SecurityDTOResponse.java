package com.futbol.estadisticas.application.port.dto.response;

import lombok.Builder;

import java.util.UUID;

public class SecurityDTOResponse {
    @Builder
    public record AuthResponse(String token) {}

    public record UsuarioResponse(
            UUID idUsuario,
            String username,
            String rol,
            boolean activo
    ) {}
}
