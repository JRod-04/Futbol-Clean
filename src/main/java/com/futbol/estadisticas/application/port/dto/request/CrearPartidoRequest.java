package com.futbol.estadisticas.application.port.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record CrearPartidoRequest(        
        @NotNull(message = "El equipo local es obligatorio")
        UUID idEquipoLocal,
 
        @NotNull(message = "El equipo visitante es obligatorio")
        UUID idEquipoVisitante,
 
        @NotNull(message = "La competición es obligatoria")
        UUID idCompeticion,
 
        @NotNull(message = "El árbitro es obligatorio")
        UUID idArbitro,
 
        @NotNull(message = "La fecha y hora es obligatoria")
        LocalDateTime fechaYHora,
 
        @Positive(message = "La jornada debe ser positiva")
        Integer jornada,
 
        UUID idEstadio
    
    ) {

}
