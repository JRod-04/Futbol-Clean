package com.futbol.estadisticas.application.port.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ActualizarGanadorRequest(
        @NotNull(message = "El equipo ganador es obligatorio")
        UUID idEquipoGanador
) {
}
