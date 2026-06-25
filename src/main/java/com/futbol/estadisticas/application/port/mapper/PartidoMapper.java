package com.futbol.estadisticas.application.port.mapper;

import org.springframework.stereotype.Component;

import com.futbol.estadisticas.application.port.dto.response.PartidoResponse;
import com.futbol.estadisticas.domain.model.Arbitro;
import com.futbol.estadisticas.domain.model.Club;
import com.futbol.estadisticas.domain.model.Competicion;
import com.futbol.estadisticas.domain.model.Estadio;
import com.futbol.estadisticas.domain.model.Partido;

@Component
public class PartidoMapper {
    
    public PartidoResponse toResponse(Partido partido) {
        Club local      = partido.getEquipoLocal();
        Club visitante  = partido.getEquipoVisitante();
        Arbitro arbitro = partido.getArbitro();
        Estadio estadio = partido.getEstadio();
        Competicion competicion = partido.getCompeticion();
 
        return new PartidoResponse(
                partido.getIdPartido(),
                partido.getFechaYHora(),
                partido.getJornada(),
                partido.getEstado(),
                partido.getEstado() != null ? partido.getEstado().getDisplayName() : null,
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
                partido.estaEnCurso(),
                partido.haFinalizado(),
                partido.esFuturo(),
                partido.esHoy()
        );
    }
}
