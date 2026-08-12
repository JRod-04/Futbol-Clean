package com.futbol.estadisticas.application.port.dto.response;

import lombok.Builder;

@Builder
public record PartidoConAlineacionResponse(
        PartidoResponse partido,
        AlineacionResponse alineacionLocal,
        AlineacionResponse alineacionVisitante
) {
}
