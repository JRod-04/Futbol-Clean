package com.futbol.estadisticas.application.port.dto.response;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record EstadisticasJugadorResponse(
        UUID idJugador,
        String nombreJugador,
        int totalPartidosJugados,
        int totalMinutosJugados,
        int totalGoles,
        int totalGolesPenal,
        int totalPenalesFallados,
        int totalAutogoles,
        int totalAsistencias,
        int totalTarjetasAmarillas,
        int totalTarjetasRojas,
        List<EstadisticasCompeticionDTO> porCompeticion
) {
}
