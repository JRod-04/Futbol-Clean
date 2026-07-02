package com.futbol.estadisticas.application.port.dto.request;

import java.time.LocalDate;

import com.futbol.estadisticas.domain.model.enums.Nacion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CrearTecnicoRequest(@NotBlank(message = "El nombre es obligatorio")
        
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,
 
        @NotBlank(message = "El apellido es obligatorio")
        String apellido,
 
        @NotNull(message = "La fecha de nacimiento es obligatoria")
        LocalDate fechaNacimiento,
 
        @NotNull(message = "La nacionalidad es obligatoria")
        Nacion nacionalidad,
        
        @NotBlank(message = "Debe tener un estilo de Juego")
        String estiloJuego,

        @NotBlank(message = "Debes agrgar una alineacion Favorita")
        String alineacionFavorita
) {

} 
