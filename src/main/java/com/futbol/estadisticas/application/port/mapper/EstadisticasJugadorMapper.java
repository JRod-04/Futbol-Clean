package com.futbol.estadisticas.application.port.mapper;

import com.futbol.estadisticas.application.port.dto.response.EstadisticasCompeticionDTO;
import com.futbol.estadisticas.application.port.dto.response.EstadisticasJugadorResponse;
import com.futbol.estadisticas.application.port.dto.response.EventoPartidoResponse;
import com.futbol.estadisticas.domain.model.Competicion;
import com.futbol.estadisticas.domain.model.EventosPartido;
import com.futbol.estadisticas.domain.model.Jugador;
import com.futbol.estadisticas.domain.model.Partido;
import com.futbol.estadisticas.domain.model.enums.TipoEvento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class EstadisticasJugadorMapper {
    private final EventosPartidoMapper eventosPartidoMapper;
    private final EstadisticasCompeticionMapper estadisticaCompeticionMapper;

    public EstadisticasJugadorResponse toResponse(Jugador jugador, List<EventosPartido> eventos) {

        List<EventosPartido> goles = eventos.stream().filter(EventosPartido::esGol).toList();
        List<EventosPartido> asistencias = eventos.stream()
                .filter(e -> e.getTipoEvento() == TipoEvento.ASISTENCIA)
                .toList();
        List<EventosPartido> tarjetas = eventos.stream().filter(EventosPartido::esTarjeta).toList();

        return EstadisticasJugadorResponse.builder()
                .idJugador(jugador.getIdPersonal())
                .nombreJugador(jugador.getNombreCompleto())
                .totalGoles(goles.size())
                .totalAsistencias(asistencias.size())
                .totalTarjetasAmarillas(contarTipo(tarjetas, TipoEvento.AMARILLA))
                .totalTarjetasRojas(contarTipo(tarjetas, TipoEvento.ROJA))
                .porCompeticion(agruparPorCompeticion(eventos))
                .build();
    }

    private List<EstadisticasCompeticionDTO> agruparPorCompeticion(List<EventosPartido> eventos) {
        Map<UUID, List<EventosPartido>> porCompeticion = eventos.stream()
                .filter(EventosPartido::esEstadisticable)
                .filter(e -> obtenerCompeticion(e) != null)
                .collect(Collectors.groupingBy(e -> obtenerCompeticion(e).getIdCompeticion()));

        return porCompeticion.values().stream()
                .map(eventosDeLaCompeticion ->
                        estadisticaCompeticionMapper.toDTO(
                                obtenerCompeticion(eventosDeLaCompeticion.get(0)),
                                eventosDeLaCompeticion))
                .sorted(Comparator.comparing(EstadisticasCompeticionDTO::nombreCompeticion))
                .toList();
    }

    private Competicion obtenerCompeticion(EventosPartido evento) {
        Partido partido = evento.getPartido();
        return partido != null ? partido.getCompeticion() : null;
    }

    private int contarTipo(List<EventosPartido> eventos, TipoEvento tipo) {
        return (int) eventos.stream().filter(e -> e.getTipoEvento() == tipo).count();
    }

    private List<EventoPartidoResponse> mapearEventos(List<EventosPartido> eventos) {
        return eventos.stream().map(eventosPartidoMapper::toResponse).toList();
    }
}
