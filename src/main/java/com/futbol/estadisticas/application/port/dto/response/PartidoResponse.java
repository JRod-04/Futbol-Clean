package com.futbol.estadisticas.application.port.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.jar.JarOutputStream;

import com.futbol.estadisticas.domain.model.enums.EstadoPartido;

import com.futbol.estadisticas.domain.model.enums.FaseTorneo;
import com.futbol.estadisticas.domain.model.enums.JornadaPartido;
import lombok.Builder;

@Builder
public record PartidoResponse(
        UUID idPartido,
        LocalDateTime fechaYHora,
        EstadoPartido estado,
        String estadoDisplayName,

        EstadoPartido finalizadoEn,

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
        FaseTorneo fase,
        JornadaPartido jornada,
        boolean enCurso,
        boolean finalizado,
        boolean esFuturo,
        boolean esHoy
) {

}
