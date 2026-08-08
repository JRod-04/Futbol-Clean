package com.futbol.estadisticas.application.port.dto.response;

import java.time.LocalDate;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.enums.Nacion;
import com.futbol.estadisticas.domain.model.enums.TipoEquipo;
import lombok.Builder;

@Builder
public record EquipoResponse(
        UUID idEquipo,
        String nombre,
        String nombreCorto,
        LocalDate fechaFundacion,
        Nacion paisEquipo,
        TipoEquipo tipoEquipo,

        int totalJugadoresActivos,
        int jugadoresDisponibles,
        int jugadoresLesionados,
        double valorPlantilla,
        double valorPlantillaEnMillones,
        String tecnicoActual,
        UUID idTecnicoActual,
        String nombreEstadio,
        UUID idEstadio
) {

}
