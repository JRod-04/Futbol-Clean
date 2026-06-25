package com.futbol.estadisticas.application.port.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record EstadioResponse(
        UUID idEstadio,
        String nombre,
        String direccion,
        Integer capacidad,
        LocalDate fechaFundacion,
        String descripcionCompleta,
        String clubPrincipal,
        UUID idClubPrincipal
) {

}
