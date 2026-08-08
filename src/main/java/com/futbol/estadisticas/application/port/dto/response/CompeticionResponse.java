package com.futbol.estadisticas.application.port.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.Equipo;
import lombok.Builder;

@Builder
public record CompeticionResponse(
    
        UUID idCompeticion,
        String nombre,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        Equipo equipoGanador,
        boolean activa,
        boolean finalizada,
        boolean noHaComenzado,
        int totalPartidos,
        int partidosJugados,
        int partidosPendientes,
        double porcentajeAvance
) {

}
