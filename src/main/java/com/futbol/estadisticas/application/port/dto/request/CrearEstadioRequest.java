package com.futbol.estadisticas.application.port.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;

public record CrearEstadioRequest(
        @NotBlank(message = "El nombre del estadio es obligatorio")
        String nombre,
 
        @NotBlank(message = "La dirección es obligatoria")
        String direccion,
 
        @Positive(message = "La capacidad debe ser positiva")
        Integer capacidad,
 
        @Past(message = "La fecha de fundación debe ser en el pasado")
        LocalDate fechaFundacion

) {

}
