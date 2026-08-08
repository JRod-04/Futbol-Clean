package com.futbol.estadisticas.application.port.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.futbol.estadisticas.application.port.dto.request.CrearEquipoRequest;
import com.futbol.estadisticas.application.port.dto.response.EquipoResponse;
import com.futbol.estadisticas.domain.model.Equipo;

@Component
public class EquipoMapper {
 public Equipo toEntity(CrearEquipoRequest request) {
        return Equipo.builder()
                .idEquipo(UUID.randomUUID())
                .nombre(request.nombre())
                .nombreCorto(request.nombreCorto())
                .tipo(request.tipo())
                .pais(request.paisEquipo())
                .fechaFundacion(request.fechaFundacion())
                .build();
    }
 
    public EquipoResponse toResponse(Equipo club) {
        var tecnico = club.getTecnicoActual();
        var estadio = club.getEstadio();
 
        return new EquipoResponse(
                club.getIdEquipo(),
                club.getNombre(),
                club.getNombreCorto(),
                club.getFechaFundacion(),
                club.getPais(),
                club.getTipo(),
                club.getJugadoresActivos().size(),
                club.getJugadoresDisponibles().size(),
                club.getJugadoresLesionados().size(),
                club.getValorPlantillaTotal(),
                club.getValorPlantillaTotal() / 1_000_000.0,
                tecnico != null ? tecnico.getNombreCompleto() : null,
                tecnico != null ? tecnico.getIdPersonal() : null,
                estadio != null ? estadio.getNombre() : null,
                estadio != null ? estadio.getIdEstadio() : null
        );
    }
}
