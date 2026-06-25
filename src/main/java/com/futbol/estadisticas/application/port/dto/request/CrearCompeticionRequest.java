package com.futbol.estadisticas.application.port.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CrearCompeticionRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,
 
        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDateTime fechaInicio,
 
        @NotNull(message = "La fecha de fin es obligatoria")
        @Future(message = "La fecha de fin debe ser futura")
        LocalDateTime fechaFin
) {

}
