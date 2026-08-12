package com.futbol.estadisticas.application.port.dto.response;

import lombok.Builder;


@Builder
public record EstadisticasPartidoJugadorResponse(
        PartidoResponse partido,
        boolean titular,
        boolean entroDesdeElBanco,
        boolean fueSustituido,
        int minutosJugados,
        String minutoEntrada,
        String minutoSalida,
        int goles,
        int golesPenal,
        int penalesFallados,
        int autogoles,
        int asistencias,
        int tarjetasAmarillas,
        int tarjetasRojas
) {
}
