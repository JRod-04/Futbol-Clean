package com.futbol.estadisticas.application.port.mapper;

import com.futbol.estadisticas.application.port.dto.response.EstadisticasPartidoJugadorResponse;
import com.futbol.estadisticas.application.port.dto.response.PartidoResponse;
import com.futbol.estadisticas.domain.model.EventosPartido;
import com.futbol.estadisticas.domain.model.Jugador;
import com.futbol.estadisticas.domain.model.Partido;
import com.futbol.estadisticas.domain.model.enums.TipoEvento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;


@Component
@RequiredArgsConstructor
public class EstadisticasPartidoMapper {

    private final PartidoMapper partidoMapper;

    public EstadisticasPartidoJugadorResponse toResponse(Partido partido, List<EventosPartido> eventosDelJugador) {
        PartidoResponse partidoResponse = partidoMapper.toResponse(partido);

        boolean titular = tieneTipo(eventosDelJugador, TipoEvento.TITULAR);
        boolean entroDesdeElBanco = tieneTipo(eventosDelJugador, TipoEvento.SUB_IN);
        boolean fueSustituido = tieneTipo(eventosDelJugador, TipoEvento.SUB_OUT);

        String minutoSustitucion = eventosDelJugador.stream()
                .filter(e -> e.getTipoEvento() == TipoEvento.SUB_OUT)
                .findFirst()
                .map(EventosPartido::getMinutoFormateado)
                .orElse(null);

        return EstadisticasPartidoJugadorResponse.builder()
                .partido(partidoResponse)
                .titular(titular)
                .entroDesdeElBanco(entroDesdeElBanco)
                .fueSustituido(fueSustituido)
                .minutoSustitucion(minutoSustitucion)
                .goles(contar(eventosDelJugador, EventosPartido::esGol))
                .asistencias(contarTipo(eventosDelJugador, TipoEvento.ASISTENCIA))
                .tarjetasAmarillas(contarTipo(eventosDelJugador, TipoEvento.AMARILLA))
                .tarjetasRojas(contarTipo(eventosDelJugador, TipoEvento.ROJA))
                .build();
    }

    private boolean tieneTipo(List<EventosPartido> eventos, TipoEvento tipo) {
        return eventos.stream().anyMatch(e -> e.getTipoEvento() == tipo);
    }

    private int contarTipo(List<EventosPartido> eventos, TipoEvento tipo) {
        return (int) eventos.stream().filter(e -> e.getTipoEvento() == tipo).count();
    }

    private int contar(List<EventosPartido> eventos, Predicate<EventosPartido> filtro) {
        return (int) eventos.stream().filter(filtro).count();
    }
}
