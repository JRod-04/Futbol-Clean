package com.futbol.estadisticas.application.port.mapper;

import com.futbol.estadisticas.application.port.dto.response.EventoPartidoResponse;
import com.futbol.estadisticas.application.port.dto.response.TandaPenalesResponse;
import com.futbol.estadisticas.domain.model.Equipo;
import com.futbol.estadisticas.domain.model.EventosPartido;
import com.futbol.estadisticas.domain.model.Partido;
import com.futbol.estadisticas.domain.model.enums.EstadoPartido;
import com.futbol.estadisticas.domain.model.enums.TipoEvento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TandaPenalesMapper {

    private final PartidoMapper partidoMapper;
    private final EventosPartidoMapper eventosMapper;

    public TandaPenalesResponse toResponse(Partido partido) {
        if (partido == null) {
            return null;
        }

        List<EventosPartido> eventosPenaltis = partido.getEventos().stream()
                .filter(e -> e.getEstadoEvento() == EstadoPartido.PENALTIS)
                .filter(e -> e.getTipoEvento() == TipoEvento.PENALTI_CONCEDIDO ||
                        e.getTipoEvento() == TipoEvento.PENALTI_ANOTADO ||
                        e.getTipoEvento() == TipoEvento.PENALTI_FALLADO)
                .collect(Collectors.toList());

        Equipo local = partido.getEquipoLocal();
        Equipo visitante = partido.getEquipoVisitante();

        // Filtrar por equipo
        List<EventoPartidoResponse> penalesLocal = eventosPenaltis.stream()
                .filter(e -> e.getEquipoFavorecido() != null)
                .filter(e -> e.getEquipoFavorecido().getIdEquipo().equals(local.getIdEquipo()))
                .sorted(Comparator.comparing(EventosPartido::getMinuto))
                .map(eventosMapper::toResponse)
                .collect(Collectors.toList());

        List<EventoPartidoResponse> penalesVisitante = eventosPenaltis.stream()
                .filter(e -> e.getEquipoFavorecido() != null)
                .filter(e -> e.getEquipoFavorecido().getIdEquipo().equals(visitante.getIdEquipo()))
                .sorted(Comparator.comparing(EventosPartido::getMinuto))
                .map(eventosMapper::toResponse)
                .collect(Collectors.toList());


        int anotadosLocal = (int) eventosPenaltis.stream()
                .filter(e -> e.getEquipoFavorecido() != null)
                .filter(e -> e.getEquipoFavorecido().getIdEquipo().equals(local.getIdEquipo()))
                .filter(e -> e.getTipoEvento() == TipoEvento.PENALTI_ANOTADO)
                .count();

        int anotadosVisitante = (int) eventosPenaltis.stream()
                .filter(e -> e.getEquipoFavorecido() != null)
                .filter(e -> e.getEquipoFavorecido().getIdEquipo().equals(visitante.getIdEquipo()))
                .filter(e -> e.getTipoEvento() == TipoEvento.PENALTI_ANOTADO)
                .count();

        UUID idGanador = null;
        String nombreGanador = null;
        if (anotadosLocal > anotadosVisitante) {
            idGanador = local.getIdEquipo();
            nombreGanador = local.getNombre();
        } else if (anotadosVisitante > anotadosLocal) {
            idGanador = visitante.getIdEquipo();
            nombreGanador = visitante.getNombre();
        }

        return TandaPenalesResponse.builder()
                .partido(partidoMapper.toResponse(partido))
                .penalesLocal(penalesLocal)
                .penalesVisitante(penalesVisitante)
                .penalesAnotadosLocal(anotadosLocal)
                .penalesAnotadosVisitante(anotadosVisitante)
                .idEquipoGanador(idGanador)
                .nombreEquipoGanador(nombreGanador)
                .build();
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
