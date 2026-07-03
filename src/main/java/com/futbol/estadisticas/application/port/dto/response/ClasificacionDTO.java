package com.futbol.estadisticas.application.port.dto.response;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

public class ClasificacionDTO {

    public record ClasificacionResponse(
            UUID idCompeticion,
            String nombreCompeticion,
            List<EquipoClasificacion> tabla
    ) {}
    @Builder
    public record EquipoClasificacion(
            UUID idClub,
            String nombreClub,
            int partidosJugados,
            int ganados,
            int empatados,
            int perdidos,
            int golesFavor,
            int golesContra,
            int diferenciaGoles,
            int puntos
    ) {
        public int diferenciaGoles() {
            return golesFavor - golesContra;
        }
    }
}
