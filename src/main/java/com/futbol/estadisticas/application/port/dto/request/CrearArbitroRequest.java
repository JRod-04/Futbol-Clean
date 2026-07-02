package com.futbol.estadisticas.application.port.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;

@Builder
public record CrearArbitroRequest( 
        
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,
 
        @NotBlank(message = "El apellido es obligatorio")
        String apellido,
 
        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message = "La fecha de nacimiento debe ser en el pasado")
        LocalDate fechaNacimiento) {

}
