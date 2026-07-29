package com.futbol.estadisticas.application.port.dto.response;

import lombok.Builder;


@Builder
public record EstadisticasPartidoJugadorResponse(
        PartidoResponse partido,
        boolean titular,
        boolean entroDesdeElBanco,
        boolean fueSustituido,
        String minutoSustitucion,
        int goles,
        int asistencias,
        int tarjetasAmarillas,
        int tarjetasRojas
) {
}
