package com.futbol.estadisticas.application.port.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.futbol.estadisticas.application.port.dto.request.CrearEstadioRequest;
import com.futbol.estadisticas.application.port.dto.response.EstadioResponse;
import com.futbol.estadisticas.domain.model.Club;
import com.futbol.estadisticas.domain.model.Estadio;


@Component
public class EstadioMapper {

    public Estadio toEntity(CrearEstadioRequest request) {
        return Estadio.builder()
                .idEstadio(UUID.randomUUID())
                .nombre(request.nombre())
                .direccion(request.direccion())
                .capacidad(request.capacidad())
                .fechaFundacion(request.fechaFundacion())
                .build();
    }
 
    public EstadioResponse toResponse(Estadio estadio) {
        Club club = estadio.getClubPrincipal();
 
        return new EstadioResponse(
                estadio.getIdEstadio(),
                estadio.getNombre(),
                estadio.getDireccion(),
                estadio.getCapacidad(),
                estadio.getFechaFundacion(),
                estadio.getDescripcionCompleta(),
                club != null ? club.getNombre() : null,
                club != null ? club.getIdEquipo() : null
        );
    }
}
