package com.futbol.estadisticas.application.port.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.futbol.estadisticas.application.port.dto.response.ContratoResponse;
import com.futbol.estadisticas.domain.model.Club;
import com.futbol.estadisticas.domain.model.Contrato;
import com.futbol.estadisticas.domain.model.PersonalDeportivo;
import com.futbol.estadisticas.domain.model.enums.EstadoContrato;

@Component
public class ContratoMapper {
 public Contrato toEntity(UUID idContrato, PersonalDeportivo personal, Club club,
                             java.time.LocalDateTime fechaInicio, java.time.LocalDateTime fechaFin,
                             Double sueldo) {
        return Contrato.builder()
                .idContrato(idContrato)
                .personal(personal)
                .club(club)
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .sueldo(sueldo)
                .estado(EstadoContrato.ACTIVO)
                .build();
    }
 
    public ContratoResponse toResponse(Contrato contrato) {
        var personal = contrato.getPersonal();
        var club = contrato.getClub();
 
        return new ContratoResponse(
                contrato.getIdContrato(),
                contrato.getFechaInicio(),
                contrato.getFechaFin(),
                contrato.getSueldo(),
                contrato.getEstado(),
                contrato.estaVigente(),
                personal != null ? personal.getIdPersonal() : null,
                personal != null ? personal.getNombreCompleto() : null,
                club != null ? club.getIdEquipo() : null,
                club != null ? club.getNombre() : null
        );
    }
}
