package com.futbol.estadisticas.application.port.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.futbol.estadisticas.application.port.dto.request.CrearClubRequest;
import com.futbol.estadisticas.application.port.dto.response.ClubResponse;
import com.futbol.estadisticas.domain.model.Club;

@Component
public class ClubMapper {
 public Club toEntity(CrearClubRequest request) {
        return Club.builder()
                .idEquipo(UUID.randomUUID())
                .nombre(request.nombre())
                .nombreCorto(request.nombreCorto())
                .pais(request.paisClub())
                .fechaFundacion(request.fechaFundacion())
                .build();
    }
 
    public ClubResponse toResponse(Club club) {
        var tecnico = club.getTecnicoActual();
        var estadio = club.getEstadio();
 
        return new ClubResponse(
                club.getIdEquipo(),
                club.getNombre(),
                club.getNombreCorto(),
                club.getFechaFundacion(),
                club.getPais(),
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
