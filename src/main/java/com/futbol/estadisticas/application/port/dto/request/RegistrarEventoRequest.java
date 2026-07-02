package com.futbol.estadisticas.application.port.dto.request;

import java.time.LocalTime;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.enums.TipoEvento;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RegistrarEventoRequest(

        @NotNull(message = "El tipo de evento es obligatorio")
        TipoEvento tipoEvento,
 
        @NotNull(message = "El minuto del evento es obligatorio")
        LocalTime minuto,
 
        UUID idPersonal,
        UUID idEquipoFavorecido,
        String descripcion
) {

}
