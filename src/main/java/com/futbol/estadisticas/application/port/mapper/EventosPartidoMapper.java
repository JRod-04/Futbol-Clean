package com.futbol.estadisticas.application.port.mapper;

import org.springframework.stereotype.Component;

import com.futbol.estadisticas.application.port.dto.response.EventoPartidoResponse;
import com.futbol.estadisticas.domain.model.EventosPartido;

@Component
public class EventosPartidoMapper {


    public EventoPartidoResponse toResponse(EventosPartido evento) {
        if (evento == null) return null;

        String minutoFormateado = evento.getMinutoFormateado();

        return EventoPartidoResponse.builder()
                .idEvento(evento.getIdEvento())
                .minuto(evento.getMinuto())
                .minutoFormateado(minutoFormateado)
                .tipoEvento(evento.getTipoEvento())
                .descripcionCompleta(evento.getDescripcionCompleta())
                .idPersonal(evento.getPersonal() != null ? evento.getPersonal().getIdPersonal() : null)
                .nombreJugador(evento.getNombreJugador())
                .nombreEquipoFavorecido(evento.getNombreEquipoFavorecido())
                .estadoEvento(evento.getEstadoEvento())
                .esGol(evento.esGol())
                .esTarjeta(evento.esTarjeta())
                .esSustitucion(evento.esSustitucion())
                .esPenalti(evento.esPenalti())
                .colorTarjeta(evento.getColorTarjeta())
                .build();
    }
}
