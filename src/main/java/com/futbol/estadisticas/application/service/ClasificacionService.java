package com.futbol.estadisticas.application.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.futbol.estadisticas.application.port.dto.response.ClasificacionDTO.*;
import com.futbol.estadisticas.application.port.in.ClasificacionUseCase;
import com.futbol.estadisticas.application.port.out.CompeticionRepositoryPort;
import com.futbol.estadisticas.application.port.out.PartidoRepositoryPort;
import com.futbol.estadisticas.domain.model.Competicion;
import com.futbol.estadisticas.domain.model.EventosPartido;
import com.futbol.estadisticas.domain.model.Partido;
import com.futbol.estadisticas.domain.model.enums.EstadoPartido;
import com.futbol.estadisticas.domain.model.enums.TipoEvento;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClasificacionService implements ClasificacionUseCase {

    private final PartidoRepositoryPort partidoRepository;
    private final CompeticionRepositoryPort competicionRepository;

    @Override
    public ClasificacionResponse obtenerTabla(UUID idCompeticion) {
        Competicion competicion = competicionRepository.findById(idCompeticion)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Competición no encontrada con id: " + idCompeticion));

        List<Partido> partidos = partidoRepository.findClasificacion(idCompeticion);

        Map<UUID, Object[]> mapa = new HashMap<>();

        for (Partido partido : partidos) {
            procesarPartido(partido,
                    partido.getEquipoLocal().getIdEquipo(),
                    partido.getEquipoLocal().getNombre(),
                    partido.getGolesLocal(),
                    partido.getGolesVisitante(),
                    mapa);

            procesarPartido(partido,
                    partido.getEquipoVisitante().getIdEquipo(),
                    partido.getEquipoVisitante().getNombre(),
                    partido.getGolesVisitante(),
                    partido.getGolesLocal(),
                    mapa);
        }

        List<EquipoClasificacion> tabla = mapa.entrySet().stream()
                .map(entry -> {
                    Object[] stats = entry.getValue();
                    return new EquipoClasificacion(
                            entry.getKey(),
                            (String) stats[0],
                            (int) stats[1],
                            (int) stats[2],
                            (int) stats[3],
                            (int) stats[4],
                            (int) stats[5],
                            (int) stats[6],
                            (int) stats[5] - (int) stats[6],
                            (int) stats[7]
                    );
                })
                .sorted(comparadorTabla())
                .collect(Collectors.toList());

        return new ClasificacionResponse(
                competicion.getIdCompeticion(),
                competicion.getNombre(),
                tabla
        );
    }

    private void procesarPartido(Partido partido, UUID idClub, String nombreClub,
                                 int golesFavor, int golesContra,
                                 Map<UUID, Object[]> mapa) {

        Object[] stats = mapa.computeIfAbsent(idClub, k -> new Object[]{
                nombreClub,  // [0] nombreEquipo
                0,           // [1] partidosJugados
                0,           // [2] ganados
                0,           // [3] empatados
                0,           // [4] perdidos
                0,           // [5] golesFavor
                0,           // [6] golesContra
                0            // [7] puntos
        });

        stats[1] = (int) stats[1] + 1;
        stats[5] = (int) stats[5] + golesFavor;
        stats[6] = (int) stats[6] + golesContra;

        if (!partido.haFinalizado() && partido.estaEnCurso()) {
            if (golesFavor > golesContra) {
                stats[7] = (int) stats[7] + 3;
            } else if (golesFavor == golesContra) {
                stats[7] = (int) stats[7] + 1;
            }
            return;
        }

        if (!partido.haFinalizado()) {
            return;
        }

        EstadoPartido finalizadoEn = obtenerEstadoFinalizacion(partido);

        if (finalizadoEn == EstadoPartido.PENALTIS) {
            procesarResultadoPenales(partido, idClub, stats);
            return;
        }

        if (golesFavor > golesContra) {
            stats[2] = (int) stats[2] + 1;
            stats[7] = (int) stats[7] + 3;
        } else if (golesFavor == golesContra) {
            stats[3] = (int) stats[3] + 1;
            stats[7] = (int) stats[7] + 1;
        } else {
            stats[4] = (int) stats[4] + 1;
        }
    }


    private void procesarResultadoPenales(Partido partido, UUID idClub, Object[] stats) {
        List<EventosPartido> penales = partido.getEventos().stream()
                .filter(e -> e.getEstadoEvento() == EstadoPartido.PENALTIS)
                .filter(e -> e.getTipoEvento() == TipoEvento.PENALTI_ANOTADO ||
                        e.getTipoEvento() == TipoEvento.PENALTI_FALLADO)
                .collect(Collectors.toList());

        long anotadosLocal = penales.stream()
                .filter(e -> e.getEquipoFavorecido() != null)
                .filter(e -> e.getEquipoFavorecido().getIdEquipo().equals(partido.getEquipoLocal().getIdEquipo()))
                .filter(e -> e.getTipoEvento() == TipoEvento.PENALTI_ANOTADO)
                .count();

        long anotadosVisitante = penales.stream()
                .filter(e -> e.getEquipoFavorecido() != null)
                .filter(e -> e.getEquipoFavorecido().getIdEquipo().equals(partido.getEquipoVisitante().getIdEquipo()))
                .filter(e -> e.getTipoEvento() == TipoEvento.PENALTI_ANOTADO)
                .count();

        boolean esLocal = partido.getEquipoLocal().getIdEquipo().equals(idClub);
        int golesClub = esLocal ? (int) anotadosLocal : (int) anotadosVisitante;
        int golesRival = esLocal ? (int) anotadosVisitante : (int) anotadosLocal;

        if (golesClub > golesRival) {
            stats[2] = (int) stats[2] + 1;
            stats[7] = (int) stats[7] + 3;
        } else {
            stats[4] = (int) stats[4] + 1;
            stats[7] = (int) stats[7] + 0;
        }
    }


    private EstadoPartido obtenerEstadoFinalizacion(Partido partido) {
        if (partido == null || partido.getEventos() == null) {
            return null;
        }
        return partido.getEventos().stream()
                .filter(e -> e.getTipoEvento() == TipoEvento.FIN_PARTIDO)
                .findFirst()
                .map(EventosPartido::getEstadoEvento)
                .orElse(null);
    }

    private Comparator<EquipoClasificacion> comparadorTabla() {
        return Comparator
                .comparing(EquipoClasificacion::puntos, Comparator.reverseOrder())
                .thenComparing(EquipoClasificacion::diferenciaGoles, Comparator.reverseOrder())
                .thenComparing(EquipoClasificacion::golesFavor, Comparator.reverseOrder())
                .thenComparing(EquipoClasificacion::partidosJugados)
                .thenComparing(EquipoClasificacion::nombreEquipo);
    }
}