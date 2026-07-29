package com.futbol.estadisticas.application.port.dto.response;

import lombok.Builder;

@Builder
public record SustitucionResponse(
        EventoPartidoResponse eventoSalida,
        EventoPartidoResponse eventoEntrada
) {
}
