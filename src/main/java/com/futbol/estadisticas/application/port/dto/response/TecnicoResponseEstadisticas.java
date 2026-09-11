package com.futbol.estadisticas.application.port.dto.response;

import com.futbol.estadisticas.domain.model.enums.Nacion;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record TecnicoResponseEstadisticas (

            UUID idPersonal,
            String nombre,
            String apellido,
            String nombreCompleto,
            LocalDate fechaNacimiento,
            int edad,
            Nacion nacionalidad,
            String estiloJuego,
            String alineacionFavorita,
            String equipoActual,
            UUID idEquipoActual,

            int partidosJugados,
            int partidosGanados,
            int partidosEmpatados,
            int partidosPerdidos
    ) {}

