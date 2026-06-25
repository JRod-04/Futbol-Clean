package com.futbol.estadisticas.application.port.dto.response;

import java.time.LocalDate;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.JuegoPies;
import com.futbol.estadisticas.domain.model.enums.Nacion;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;

import lombok.Builder;

@Builder
public record JugadorResponse(

        UUID idPersonal,
        String nombre,
        String apellido,
        String nombreCompleto,
        LocalDate fechaNacimiento,
        int edad,
        Nacion nacionalidad,
        JuegoPies pieHabil,
        Integer altura,
        Integer peso,
        PosicionJugador posicion,
        String posicionDisplayName,
        EstadoJugador estadoJugador,
        Double valorMercado,
        Double valorMercadoEnMillones,
        String clubActual,
        UUID idClubActual,
        boolean disponible,
        int lesionesActivas
) {

}
