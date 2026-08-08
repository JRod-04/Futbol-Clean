package com.futbol.estadisticas.application.port.dto.response;

import java.time.LocalDate;
import java.util.List;
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
        List<PosicionJugador> posiciones,
        Integer dorsal,
        EstadoJugador estadoJugador,
        Double valorMercado,
        Double valorMercadoEnMillones,
        String equipoActual,
        UUID idEquipoActual,
        boolean disponible,
        int lesionesActivas
) {

}
