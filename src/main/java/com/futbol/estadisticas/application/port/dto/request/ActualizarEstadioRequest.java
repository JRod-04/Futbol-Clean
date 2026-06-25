package com.futbol.estadisticas.application.port.dto.request;

import jakarta.validation.constraints.Positive;

public record ActualizarEstadioRequest(
        String nombre,
        String direccion,
 
        @Positive(message = "La capacidad debe ser positiva")
        Integer capacidad
) {

}
