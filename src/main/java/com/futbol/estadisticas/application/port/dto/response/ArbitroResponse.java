package com.futbol.estadisticas.application.port.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record ArbitroResponse(
        UUID idArbitro,
        String nombre,
        String apellido,
        String nombreCompleto,
        LocalDate fechaNacimiento,
        int edad,
        int totalPartidosArbitrados
) {
 
}
