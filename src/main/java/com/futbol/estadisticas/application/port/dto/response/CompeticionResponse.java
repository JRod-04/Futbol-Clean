package com.futbol.estadisticas.application.port.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record CompeticionResponse(
    
        UUID idCompeticion,
        String nombre,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        boolean activa,
        boolean finalizada,
        boolean noHaComenzado,
        int totalPartidos,
        int partidosJugados,
        int partidosPendientes,
        double porcentajeAvance
) {

}
