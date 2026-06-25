package com.futbol.estadisticas.application.port.dto.response;

import java.time.LocalTime;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.enums.TipoEvento;

public record EventoPartidoResponse(
        UUID idEvento,
        LocalTime minuto,
        String minutoFormateado,
        TipoEvento tipoEvento,
        String descripcionCompleta,
 
        UUID idPersonal,
        String nombreJugador,
 
        String nombreEquipoFavorecido,
 
        boolean esGol,
        boolean esTarjeta,
        boolean esSustitucion,
        boolean esPenalti,
        String colorTarjeta
) {

}
