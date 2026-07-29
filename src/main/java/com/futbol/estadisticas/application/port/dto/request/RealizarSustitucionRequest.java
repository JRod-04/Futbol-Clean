package com.futbol.estadisticas.application.port.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalTime;
import java.util.UUID;


@Builder
public record RealizarSustitucionRequest(
        @NotNull(message = "El jugador entrante es obligatorio")
        UUID idJugadorEntrante,

        @NotNull(message = "El jugador saliente es obligatorio")
        UUID idJugadorSaliente,

        @NotNull(message = "El club es obligatorio")
        UUID idClub,

        @NotNull(message = "El minuto de la sustitución es obligatorio")
        LocalTime minuto
) {
}
