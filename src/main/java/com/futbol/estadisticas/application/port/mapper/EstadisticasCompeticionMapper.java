package com.futbol.estadisticas.application.port.mapper;

import com.futbol.estadisticas.application.port.dto.response.EstadisticasCompeticionDTO;
import com.futbol.estadisticas.domain.model.Competicion;
import com.futbol.estadisticas.domain.model.EventosPartido;
import com.futbol.estadisticas.domain.model.enums.TipoEvento;
import lombok.Builder;
import org.springframework.stereotype.Component;

import java.util.List;


@Builder
@Component
public class EstadisticasCompeticionMapper {

    public EstadisticasCompeticionDTO toDTO(Competicion competicion, List<EventosPartido> eventosDeLaCompeticion) {
        if (competicion == null) return null;

        return EstadisticasCompeticionDTO.builder()
                .idCompeticion(competicion.getIdCompeticion())
                .nombreCompeticion(competicion.getNombre())
                .goles(contarGoles(eventosDeLaCompeticion))
                .asistencias(contarTipo(eventosDeLaCompeticion, TipoEvento.ASISTENCIA))
                .tarjetasAmarillas(contarTipo(eventosDeLaCompeticion, TipoEvento.AMARILLA))
                .tarjetasRojas(contarTipo(eventosDeLaCompeticion, TipoEvento.ROJA))
                .build();
    }

    private int contarGoles(List<EventosPartido> eventos) {
        return (int) eventos.stream().filter(EventosPartido::esGol).count();
    }

    private int contarTipo(List<EventosPartido> eventos, TipoEvento tipo) {
        return (int) eventos.stream().filter(e -> e.getTipoEvento() == tipo).count();
    }
}
