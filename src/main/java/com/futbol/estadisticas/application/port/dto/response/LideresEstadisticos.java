package com.futbol.estadisticas.application.port.dto.response;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

public class LideresEstadisticos {
    @Builder
    public record LideresEstadisticosResponse(
            UUID idCompeticion,
            String nombreCompeticion,
            List<EstadisticaLideresDTO> estadisticas
    ) {}

    @Builder
    public record EstadisticaLideresDTO(
            String tituloEstadistica,
            List<JugadorLiderDTO> jugadores
    ) {}

    @Builder
    public record JugadorLiderDTO(
            UUID idJugador,
            String nombre,
            String apellido,
            String nombreCompleto,
            Integer dorsal,
            String nombreEquipo,
            UUID idEquipo,
            Integer valorInt,
            Double valorDecimal
    ) {}
}
