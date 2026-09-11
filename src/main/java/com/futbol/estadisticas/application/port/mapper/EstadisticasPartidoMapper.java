package com.futbol.estadisticas.application.port.mapper;

import com.futbol.estadisticas.application.port.dto.response.EstadisticasPartidoJugadorResponse;
import com.futbol.estadisticas.application.port.dto.response.PartidoResponse;
import com.futbol.estadisticas.domain.model.EventosPartido;
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

        String minutoEntrada = eventosDelJugador.stream()
                .filter(e -> e.getTipoEvento() == TipoEvento.SUB_IN)
                .findFirst()
                .map(EventosPartido::getMinutoFormateado)
                .orElse(null);

        int minutosJugados = calcularMinutosJugados(partido, eventosDelJugador);


        return EstadisticasPartidoJugadorResponse.builder()
                .partido(partidoResponse)
                .titular(titular)
                .entroDesdeElBanco(entroDesdeElBanco)
                .fueSustituido(fueSustituido)
                .minutoEntrada(minutoEntrada)
                .minutoSalida(minutoSustitucion)
                .minutosJugados(minutosJugados)
                .goles(contar(eventosDelJugador, EventosPartido::esGol))
                .golesPenal(contar(eventosDelJugador, EventosPartido::esGolDePenal))
                .penalesFallados(contar(eventosDelJugador, EventosPartido::esPenalFallado))
                .autogoles(contar(eventosDelJugador, EventosPartido::esAutoGol))
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

    private int calcularMinutosJugados(Partido partido, List<EventosPartido> eventosDelJugador) {
        EventosPartido entrada = eventosDelJugador.stream()
                .filter(e -> e.getTipoEvento() == TipoEvento.TITULAR ||
                        e.getTipoEvento() == TipoEvento.SUB_IN)
                .findFirst()
                .orElse(null);

        if (entrada == null || entrada.getMinuto() == null) {
            return 0;
        }

        int minutoEntrada = entrada.getMinuto().getHour() * 60 + entrada.getMinuto().getMinute();

        EventosPartido salida = eventosDelJugador.stream()
                .filter(e -> e.getTipoEvento() == TipoEvento.SUB_OUT)
                .findFirst()
                .orElse(null);

        if (salida != null && salida.getMinuto() != null) {
            int minutoSalida = salida.getMinuto().getHour() * 60 + salida.getMinuto().getMinute();
            return Math.max(0, minutoSalida - minutoEntrada);
        }

        EventosPartido finPartido = partido.getEventos().stream()
                .filter(e -> e.getTipoEvento() == TipoEvento.FIN_PARTIDO)
                .findFirst()
                .orElse(null);

        if (finPartido != null && finPartido.getMinuto() != null) {
            int minutoFin = finPartido.getMinuto().getHour() * 60 + finPartido.getMinuto().getMinute();
            return Math.max(0, minutoFin - minutoEntrada);
        }

        return Math.max(0, 90 - minutoEntrada);
    }

}
