package com.futbol.estadisticas.application.port.mapper;

import com.futbol.estadisticas.application.port.dto.response.AlineacionResponse;
import com.futbol.estadisticas.application.port.dto.response.JugadorPosicionNotificacionDTO;
import com.futbol.estadisticas.application.port.dto.response.JugadorPosicionResponse;
import com.futbol.estadisticas.application.port.dto.response.PartidoConAlineacionResponse;
import com.futbol.estadisticas.domain.model.Equipo;
import com.futbol.estadisticas.domain.model.EventosPartido;
import com.futbol.estadisticas.domain.model.Partido;
import com.futbol.estadisticas.domain.model.enums.Alineacion;
import com.futbol.estadisticas.domain.model.enums.TipoEvento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AlineacionMapper {

    public AlineacionResponse toResponse(Equipo equipo, Alineacion alineacion,
                                         List<JugadorPosicionNotificacionDTO> jugadoresConPosicion) {
        List<JugadorPosicionResponse> titularesResponse = jugadoresConPosicion.stream()
                .map(dto -> JugadorPosicionResponse.builder()
                        .idJugador(dto.jugador().getIdPersonal())
                        .nombre(dto.jugador().getNombre())
                        .apellido(dto.jugador().getApellido())
                        .posicionenPartido(dto.posicionNueva().getDisplayName())
                        .dorsal(dto.jugador().getDatosDeportivos() != null ?
                                dto.jugador().getDatosDeportivos().getDorsal() : null)
                        .build())
                .toList();

        return AlineacionResponse.builder()
                .idEquipo(equipo.getIdEquipo())
                .nombreEquipo(equipo.getNombre())
                .nombreAlineacion(alineacion)
                .titulares(titularesResponse)
                .build();
    }


    public PartidoConAlineacionResponse toPartidoWithAlineacion(Partido partido,
                                                                AlineacionResponse alineacionLocal,
                                                                AlineacionResponse alineacionVisitante,
                                                                PartidoMapper partidoMapper) {
        return PartidoConAlineacionResponse.builder()
                .partido(partidoMapper.toResponse(partido))
                .alineacionLocal(alineacionLocal)
                .alineacionVisitante(alineacionVisitante)
                .build();
    }
}