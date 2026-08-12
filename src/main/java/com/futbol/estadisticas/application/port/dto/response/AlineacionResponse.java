package com.futbol.estadisticas.application.port.dto.response;

import com.futbol.estadisticas.domain.model.enums.Alineacion;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record AlineacionResponse (
        UUID idEquipo,
        String nombreEquipo,
        Alineacion nombreAlineacion,
        List<JugadorPosicionResponse> titulares
)

{ }
