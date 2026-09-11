package com.futbol.estadisticas.application.port.dto.response;

import com.futbol.estadisticas.domain.model.enums.FaseTorneo;
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
            UUID idEquipo,
            String nombreEquipo,
            String nombreCortoEquipo,
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

    public record ClasificacionGruposResponse(
            UUID idCompeticion,
            String nombreCompeticion,
            List<GrupoClasificacion> grupos
    ) {}

    public record GrupoClasificacion(
            FaseTorneo nombreGrupo,
            List<EquipoClasificacion> tabla
    ) {}
}
