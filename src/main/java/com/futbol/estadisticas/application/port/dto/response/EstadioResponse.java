package com.futbol.estadisticas.application.port.dto.response;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Builder;

@Builder
public record EstadioResponse(
        UUID idEstadio,
        String nombre,
        String direccion,
        Integer capacidad,
        LocalDate fechaFundacion,
        String descripcionCompleta,
        String equipoPrincipal,
        UUID idEquipoPrincipal
) {

}
