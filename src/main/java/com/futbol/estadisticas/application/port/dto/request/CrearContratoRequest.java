package com.futbol.estadisticas.application.port.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.enums.EstadoContrato;
import com.futbol.estadisticas.domain.model.enums.TipoContrato;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record CrearContratoRequest(
        @NotNull(message = "El personal es obligatorio")
        UUID idPersonal,
 
        @NotNull(message = "El equipo es obligatorio")
        UUID idEquipo,

        @NotNull(message = "Debe especificar el tipo de contrato")
        TipoContrato tipoContrato,

        Double costoFichaje,

        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDateTime fechaInicio,

        @NotNull(message = "El estado Contrato es obligatorio")
        EstadoContrato estado,

        @NotNull(message = "La fecha de fin es obligatoria")
        LocalDateTime fechaFin,
 
        @NotNull(message = "El sueldo es obligatorio")
        @Positive(message = "El sueldo debe ser positivo")
        Double sueldo
) {

}
