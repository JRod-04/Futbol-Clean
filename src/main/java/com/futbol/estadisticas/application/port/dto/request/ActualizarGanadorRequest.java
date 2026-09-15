package com.futbol.estadisticas.application.port.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ActualizarGanadorRequest(
        @NotNull(message = "El equipo ganador es obligatorio")
        UUID idEquipoGanador
) {
}
