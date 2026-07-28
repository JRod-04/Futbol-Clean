package com.futbol.estadisticas.application.port.dto.response;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record EstadisticasJugadorResponse(
        UUID idJugador,
        String nombreJugador,
        int totalGoles,
        int totalAsistencias,
        int totalTarjetasAmarillas,
        int totalTarjetasRojas,
        List<EstadisticasCompeticionDTO> porCompeticion


) {
}
