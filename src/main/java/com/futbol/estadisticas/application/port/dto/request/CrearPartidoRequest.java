package com.futbol.estadisticas.application.port.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.enums.FaseTorneo;
import com.futbol.estadisticas.domain.model.enums.JornadaPartido;
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

        FaseTorneo fase,
        JornadaPartido jornadaTorneo,
        UUID idEstadio
    
    ) {

}
