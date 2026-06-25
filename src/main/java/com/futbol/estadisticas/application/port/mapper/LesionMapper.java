package com.futbol.estadisticas.application.port.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.futbol.estadisticas.application.port.dto.request.RegistrarLesionRequest;
import com.futbol.estadisticas.application.port.dto.response.LesionResponse;
import com.futbol.estadisticas.domain.model.Jugador;
import com.futbol.estadisticas.domain.model.Lesion;

@Component
public class LesionMapper {
     public Lesion toEntity(RegistrarLesionRequest request) {
        return Lesion.builder()
                .idLesion(UUID.randomUUID())
                .nombreLesion(request.nombreLesion())
                .gravedad(request.gravedad())
                .fechaInicio(request.fechaInicio())
                .fechaFin(request.fechaFinEstimada())
                .curada(false)
                .build();
    }
 
    public LesionResponse toResponse(Lesion lesion, Jugador jugador) {
        return new LesionResponse(
                lesion.getIdLesion(),
                lesion.getNombreLesion(),
                lesion.getGravedad(),
                lesion.getFechaInicio(),
                lesion.getFechaFin(),
                lesion.isCurada(),
                lesion.esActiva(),
                lesion.esGrave(),
                lesion.necesitaAtencionUrgente(),
                lesion.getEstadoLesion(),
                lesion.getDuracionDias(),
                lesion.getDiasRestantesRecuperacion(),
                jugador != null ? jugador.getIdPersonal() : null,
                jugador != null ? jugador.getNombreCompleto() : null
        );
    }
}
