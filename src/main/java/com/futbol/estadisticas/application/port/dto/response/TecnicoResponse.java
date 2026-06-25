package com.futbol.estadisticas.application.port.dto.response;

import java.time.LocalDate;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.enums.Nacion;

public record TecnicoResponse(
        UUID idPersonal,
        String nombre,
        String apellido,
        String nombreCompleto,
        LocalDate fechaNacimiento,
        int edad,
        Nacion nacionalidad,
        String estiloJuego,
        String alineacionFavorita,
        String clubActual,
        UUID idClubActual
) {

}
