package com.futbol.estadisticas.application.port.dto.response;

import java.time.LocalDate;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.enums.Nacion;
import lombok.Builder;

@Builder
public record ClubResponse(
        UUID idEquipo,
        String nombre,
        String nombreCorto,
        LocalDate fechaFundacion,
        Nacion paisClub,
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
