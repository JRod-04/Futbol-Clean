package com.futbol.estadisticas.application.port.mapper;

import java.util.UUID;

import com.futbol.estadisticas.domain.model.enums.TipoContrato;
import org.springframework.stereotype.Component;

import com.futbol.estadisticas.application.port.dto.response.ContratoResponse;
import com.futbol.estadisticas.domain.model.Equipo;
import com.futbol.estadisticas.domain.model.Contrato;
import com.futbol.estadisticas.domain.model.PersonalDeportivo;
import com.futbol.estadisticas.domain.model.enums.EstadoContrato;

@Component
public class ContratoMapper {
 public Contrato toEntity(UUID idContrato, PersonalDeportivo personal, Equipo equipo,
                          java.time.LocalDateTime fechaInicio, java.time.LocalDateTime fechaFin,
                          EstadoContrato estado, Double sueldo, Double costoFichaje, TipoContrato tipoContrato) {
        return Contrato.builder()
                .idContrato(idContrato)
                .personal(personal)
                .equipo(equipo)
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .sueldo(sueldo)
                .costoFichaje(costoFichaje)
                .tipoContrato(tipoContrato)
                .estado(estado)
                .build();
    }
 
    public ContratoResponse toResponse(Contrato contrato) {
        var personal = contrato.getPersonal();
        var equipo = contrato.getEquipo();
 
        return new ContratoResponse(
                contrato.getIdContrato(),
                contrato.getFechaInicio(),
                contrato.getFechaFin(),
                contrato.getSueldo(),
                contrato.getTipoContrato(),
                contrato.getEstado(),
                contrato.estaVigente(),
                personal != null ? personal.getIdPersonal() : null,
                personal != null ? personal.getNombreCompleto() : null,
                equipo != null ? equipo.getIdEquipo() : null,
                equipo != null ? equipo.getNombre() : null,
                contrato.getCostoFichaje()
                );
    }
}
