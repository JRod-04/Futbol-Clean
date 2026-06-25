package com.futbol.estadisticas.application.port.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.enums.EstadoPartido;

public record PartidoResponse(
        UUID idPartido,
        LocalDateTime fechaYHora,
        Integer jornada,
        EstadoPartido estado,
        String estadoDisplayName,
 
        UUID idEquipoLocal,
        String nombreEquipoLocal,
 
        UUID idEquipoVisitante,
        String nombreEquipoVisitante,
 
        int golesLocal,
        int golesVisitante,
        String resultado,
 
        String nombreArbitro,
        String nombreEstadio,
        String nombreCompeticion,
        UUID idCompeticion,
 
        boolean enCurso,
        boolean finalizado,
        boolean esFuturo,
        boolean esHoy
) {

}
