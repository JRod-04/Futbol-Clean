package com.futbol.estadisticas.application.port.mapper;

import java.util.List;
import java.util.UUID;

import com.futbol.estadisticas.application.port.dto.response.TecnicoResponseEstadisticas;
import com.futbol.estadisticas.domain.model.Partido;
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

    public TecnicoResponseEstadisticas toResponseConEstadisticas(Tecnico tecnico, List<Partido> partidosDirigidos) {
        Equipo club = tecnico.getClubActual();

        int partidosJugados = 0;
        int partidosGanados = 0;
        int partidosEmpatados = 0;
        int partidosPerdidos = 0;

        if (partidosDirigidos != null && !partidosDirigidos.isEmpty()) {
            List<Partido> partidosFinalizados = partidosDirigidos.stream()
                    .filter(Partido::haFinalizado)
                    .toList();

            partidosJugados = partidosFinalizados.size();

            for (Partido partido : partidosFinalizados) {
                UUID idEquipoTecnico = partido.getEventos().stream()
                        .filter(e -> e.getPersonal() != null &&
                                e.getPersonal().getIdPersonal().equals(tecnico.getIdPersonal()) &&
                                e.getTipoEvento() == com.futbol.estadisticas.domain.model.enums.TipoEvento.DT_PARTIDO)
                        .findFirst()
                        .map(e -> e.getEquipoFavorecido() != null ? e.getEquipoFavorecido().getIdEquipo() : null)
                        .orElse(null);

                if (idEquipoTecnico == null && club != null) {
                    idEquipoTecnico = club.getIdEquipo();
                }

                if (idEquipoTecnico == null) continue;

                boolean esLocal = partido.getEquipoLocal() != null &&
                        partido.getEquipoLocal().getIdEquipo().equals(idEquipoTecnico);

                int golesEquipo = esLocal ? partido.getGolesLocal() : partido.getGolesVisitante();
                int golesRival = esLocal ? partido.getGolesVisitante() : partido.getGolesLocal();

                if (golesEquipo > golesRival) {
                    partidosGanados++;
                } else if (golesEquipo < golesRival) {
                    partidosPerdidos++;
                } else {
                    partidosEmpatados++;
                }
            }
        }

        return TecnicoResponseEstadisticas.builder()
                .idPersonal(tecnico.getIdPersonal())
                .nombre(tecnico.getNombre())
                .apellido(tecnico.getApellido())
                .nombreCompleto(tecnico.getNombreCompleto())
                .fechaNacimiento(tecnico.getFechaNacimiento())
                .edad(tecnico.getEdad())
                .nacionalidad(tecnico.getNacionalidad())
                .estiloJuego(tecnico.getEstiloJuego())
                .alineacionFavorita(tecnico.getAlineacionFavorita())
                .equipoActual(club != null ? club.getNombre() : null)
                .idEquipoActual(club != null ? club.getIdEquipo() : null)
                .partidosJugados(partidosJugados)
                .partidosGanados(partidosGanados)
                .partidosEmpatados(partidosEmpatados)
                .partidosPerdidos(partidosPerdidos)
                .build();
    }
}
