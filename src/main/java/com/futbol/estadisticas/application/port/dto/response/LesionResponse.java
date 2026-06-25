package com.futbol.estadisticas.application.port.dto.response;

import java.time.LocalDate;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.enums.Gravedad;

public record LesionResponse(
    
        UUID idLesion,
        String nombreLesion,
        Gravedad gravedad,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        boolean curada,
        boolean activa,
        boolean grave,
        boolean necesitaAtencionUrgente,
        String estadoLesion,
        long duracionDias,
        long diasRestantesRecuperacion,
 
        UUID idJugador,
        String nombreJugador
) {

}
