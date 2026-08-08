package com.futbol.estadisticas.application.port.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.futbol.estadisticas.application.port.dto.request.CrearTecnicoRequest;
import com.futbol.estadisticas.application.port.dto.response.TecnicoResponse;
import com.futbol.estadisticas.domain.model.Equipo;
import com.futbol.estadisticas.domain.model.Tecnico;
import com.futbol.estadisticas.domain.model.enums.TipoPersonal;

@Component
public class TecnicoMapper {
     public Tecnico toEntity(CrearTecnicoRequest request) {
        return Tecnico.builder()
                .idPersonal(UUID.randomUUID())
                .nombre(request.nombre())
                .apellido(request.apellido())
                .fechaNacimiento(request.fechaNacimiento())
                .nacionalidad(request.nacionalidad())
                .tipoPersonal(TipoPersonal.TECNICO)
                .estiloJuego(request.estiloJuego())
                .alineacionFavorita(request.alineacionFavorita())
                .build();
    }
 
    public TecnicoResponse toResponse(Tecnico tecnico) {
        Equipo club = tecnico.getClubActual();
 
        return new TecnicoResponse(
                tecnico.getIdPersonal(),
                tecnico.getNombre(),
                tecnico.getApellido(),
                tecnico.getNombreCompleto(),
                tecnico.getFechaNacimiento(),
                tecnico.getEdad(),
                tecnico.getNacionalidad(),
                tecnico.getEstiloJuego(),
                tecnico.getAlineacionFavorita(),
                club != null ? club.getNombre() : null,
                club != null ? club.getIdEquipo() : null
        );
    }
}
