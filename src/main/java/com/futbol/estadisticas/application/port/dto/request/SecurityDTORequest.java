package com.futbol.estadisticas.application.port.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public class SecurityDTORequest {
    @Builder
    public record LoginRequest(
            @NotBlank String username,
            @NotBlank String password
    ) {}

    @Builder
    public record RegisterRequest(
            @NotBlank String username,
            @NotBlank String password
    ) {}
    public record CrearUsuarioRequest(
            @NotBlank(message = "El username es obligatorio")
            @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
            String username,

            @NotBlank(message = "La contraseña es obligatoria")
            @Size(min = 8, max = 100, message = "La contraseña debe tener al menos 8 caracteres")
            String password,

            String rol
    ) {}
}

