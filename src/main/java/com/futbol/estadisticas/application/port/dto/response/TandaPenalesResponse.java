package com.futbol.estadisticas.application.port.dto.response;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record TandaPenalesResponse(
        PartidoResponse partido,

        int penalesAnotadosLocal,
        int penalesAnotadosVisitante,

        UUID idEquipoGanador,
        String nombreEquipoGanador,

        List<EventoPartidoResponse> penalesLocal,
        List<EventoPartidoResponse> penalesVisitante

        ) {
}
