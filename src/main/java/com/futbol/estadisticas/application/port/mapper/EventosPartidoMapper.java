package com.futbol.estadisticas.application.port.mapper;

import org.springframework.stereotype.Component;

import com.futbol.estadisticas.application.port.dto.response.EventoPartidoResponse;
import com.futbol.estadisticas.domain.model.Club;
import com.futbol.estadisticas.domain.model.EventosPartido;
import com.futbol.estadisticas.domain.model.PersonalDeportivo;

@Component
public class EventosPartidoMapper {
 
     
    public EventoPartidoResponse toResponse(EventosPartido evento) {
        PersonalDeportivo personal = evento.getPersonal();
        Club equipo = evento.getEquipoFavorecido();
 
        return new EventoPartidoResponse(
                evento.getIdEvento(),
                evento.getMinuto(),
                evento.getMinutoFormateado(),
                evento.getTipoEvento(),
                evento.getDescripcionCompleta(),
                personal != null ? personal.getIdPersonal() : null,
                evento.getNombreJugador(),
                evento.getNombreEquipoFavorecido(),
                evento.esGol(),
                evento.esTarjeta(),
                evento.esSustitucion(),
                evento.esPenalti(),
                evento.getColorTarjeta()
        );
    }
}
