package com.futbol.estadisticas.application.port.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.futbol.estadisticas.application.port.dto.request.CrearArbitroRequest;
import com.futbol.estadisticas.application.port.dto.response.ArbitroResponse;
import com.futbol.estadisticas.domain.model.Arbitro;

@Component
public class ArbitroMapper {
  public Arbitro toEntity(CrearArbitroRequest request) {
        return Arbitro.builder()
                .idArbitro(UUID.randomUUID())
                .nombre(request.nombre())
                .apellido(request.apellido())
                .fechaNacimiento(request.fechaNacimiento())
                .build();
    }
 
    public ArbitroResponse toResponse(Arbitro arbitro) {
        return new ArbitroResponse(
                arbitro.getIdArbitro(),
                arbitro.getNombre(),
                arbitro.getApellido(),
                arbitro.getNombreCompleto(),
                arbitro.getFechaNacimiento(),
                arbitro.getEdad(),
                arbitro.getCantidadPartidos()
        );
    }
}
