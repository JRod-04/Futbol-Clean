package com.futbol.estadisticas.application.port.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record EstadisticasCompeticionDTO(
        UUID idCompeticion,
        String nombreCompeticion,
        int goles,
        int asistencias,
        int tarjetasAmarillas,
        int tarjetasRojas

) {
}
