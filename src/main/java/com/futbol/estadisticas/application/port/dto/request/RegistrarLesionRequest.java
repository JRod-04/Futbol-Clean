package com.futbol.estadisticas.application.port.dto.request;

import java.time.LocalDate;

import com.futbol.estadisticas.domain.model.enums.Gravedad;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;

@Builder
public record RegistrarLesionRequest(
    
        @NotBlank(message = "El nombre de la lesión es obligatorio")
        String nombreLesion,
 
        @NotNull(message = "La gravedad es obligatoria")
        Gravedad gravedad,
 
        @NotNull(message = "La fecha de inicio es obligatoria")
        @PastOrPresent(message = "La fecha de inicio no puede ser futura")
        LocalDate fechaInicio,
 
        LocalDate fechaFinEstimada
) {

}
