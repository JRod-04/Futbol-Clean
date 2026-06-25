package com.futbol.estadisticas.application.port.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record ClubResponse(
        UUID idEquipo,
        String nombre,
        String nombreCorto,
        LocalDate fechaFundacion,
        int totalJugadoresActivos,
        int jugadoresDisponibles,
        int jugadoresLesionados,
        double valorPlantilla,
        double valorPlantillaEnMillones,
        String tecnicoActual,
        UUID idTecnicoActual,
        String estadio
) {

}
