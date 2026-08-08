package com.futbol.estadisticas.application.port.mapper;

import com.futbol.estadisticas.domain.model.*;
import com.futbol.estadisticas.domain.model.enums.EstadoPartido;
import com.futbol.estadisticas.domain.model.enums.TipoEvento;
import org.springframework.stereotype.Component;

import com.futbol.estadisticas.application.port.dto.response.PartidoResponse;

@Component
public class PartidoMapper {
    
    public PartidoResponse toResponse(Partido partido) {
        Equipo local      = partido.getEquipoLocal();
        Equipo visitante  = partido.getEquipoVisitante();
        Arbitro arbitro = partido.getArbitro();
        Estadio estadio = partido.getEstadio();
        Competicion competicion = partido.getCompeticion();

        EstadoPartido finalizadoEn = obtenerEstadoFinalizacion(partido);

        return new PartidoResponse(
                partido.getIdPartido(),
                partido.getFechaYHora(),
                partido.getEstado(),
                partido.getEstado() != null ? partido.getEstado().getDisplayName() : null,
                finalizadoEn,
                local != null ? local.getIdEquipo() : null,
                local != null ? local.getNombre() : null,
                visitante != null ? visitante.getIdEquipo() : null,
                visitante != null ? visitante.getNombre() : null,
                partido.getGolesLocal(),
                partido.getGolesVisitante(),
                partido.getResultado(),
                arbitro != null ? arbitro.getNombreCompleto() : null,
                estadio != null ? estadio.getNombre() : null,
                competicion != null ? competicion.getNombre() : null,
                competicion != null ? competicion.getIdCompeticion() : null,
                partido.getFase(),
                partido.getJornada(),
                partido.estaEnCurso(),
                partido.haFinalizado(),
                partido.esFuturo(),
                partido.esHoy()

        );
    }

    private EstadoPartido obtenerEstadoFinalizacion(Partido partido) {
        if (partido == null || partido.getEventos() == null) {
            return null;
        }

        return partido.getEventos().stream()
                .filter(e -> e.getTipoEvento() == TipoEvento.FIN_PARTIDO)
                .findFirst()
                .map(EventosPartido::getEstadoEvento)
                .orElse(null);
    }
}
