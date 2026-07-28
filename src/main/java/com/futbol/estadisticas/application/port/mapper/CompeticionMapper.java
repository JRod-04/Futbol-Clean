
package com.futbol.estadisticas.application.port.mapper;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.futbol.estadisticas.application.port.dto.request.CrearCompeticionRequest;
import com.futbol.estadisticas.application.port.dto.response.CompeticionResponse;
import com.futbol.estadisticas.domain.model.Competicion;
import com.futbol.estadisticas.domain.model.Partido;

@Component
public class CompeticionMapper {
     public Competicion toEntity(CrearCompeticionRequest request) {
        return Competicion.builder()
                .idCompeticion(UUID.randomUUID())
                .nombre(request.nombre())
                .fechaInicio(request.fechaInicio())
                .fechaFin(request.fechaFin())
                .build();
    }

    public CompeticionResponse toResponse(Competicion competicion) {
        List<Partido> partidos = competicion.getPartidos();
        long jugados = partidos.stream().filter(Partido::haFinalizado).count();
        long pendientes = partidos.size() - jugados;

        return new CompeticionResponse(
                competicion.getIdCompeticion(),
                competicion.getNombre(),
                competicion.getFechaInicio(),
                competicion.getFechaFin(),
                competicion.getEquipoGanador(),
                competicion.estaActiva(),
                competicion.haFinalizado(),
                competicion.noHaComenzado(),
                partidos.size(),
                (int) jugados,
                (int) pendientes,
                competicion.getPorcentajePartidosJugados()
        );
    }
}
