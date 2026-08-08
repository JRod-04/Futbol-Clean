package com.futbol.estadisticas.application.port.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.enums.EstadoContrato;

import com.futbol.estadisticas.domain.model.enums.TipoContrato;
import lombok.Builder;

@Builder
public record ContratoResponse(
        UUID idContrato,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        Double sueldo,
        TipoContrato tipo,
        EstadoContrato estado,
        boolean vigente,
 
        UUID idPersonal,
        String nombrePersonal,
 
        UUID idEquipo,
        String nombreEquipo,
        Double costoFichaje
) {

}
