package com.futbol.estadisticas.application.port.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.enums.EstadoContrato;

public record ContratoResponse(
        UUID idContrato,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        Double sueldo,
        EstadoContrato estado,
        boolean vigente,
 
        UUID idPersonal,
        String nombrePersonal,
 
        UUID idClub,
        String nombreClub
) {

}
