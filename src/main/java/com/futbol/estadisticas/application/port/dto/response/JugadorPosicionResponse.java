package com.futbol.estadisticas.application.port.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record JugadorPosicionResponse(
        UUID idJugador,
        String nombreCompleto,
        String posicionenPartido,
        Integer dorsal
){
}
