package com.futbol.estadisticas.application.service;

import java.util.*;
import java.util.stream.Collectors;

import com.futbol.estadisticas.domain.model.Equipo;
import com.futbol.estadisticas.domain.model.enums.FaseTorneo;
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



    private final Set<FaseTorneo> FASES_GRUPO = Set.of(
            FaseTorneo.GRUPO_A,
            FaseTorneo.GRUPO_B,
            FaseTorneo.GRUPO_C,
            FaseTorneo.GRUPO_D,
            FaseTorneo.GRUPO_E,
            FaseTorneo.GRUPO_F,
            FaseTorneo.GRUPO_G,
            FaseTorneo.GRUPO_H,
            FaseTorneo.GRUPO_I,
            FaseTorneo.GRUPO_J,
            FaseTorneo.GRUPO_K,
            FaseTorneo.GRUPO_L
    );

    private final Set<FaseTorneo> FASES_LIGA = Set.of(
            FaseTorneo.LIGA,
            FaseTorneo.FASE_LIGA,
            FaseTorneo.CLASIFICATORIA
    );

    @Override
    public Object obtenerTabla(UUID idCompeticion) {
        Competicion competicion = competicionRepository.findById(idCompeticion)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Competición no encontrada con id: " + idCompeticion));

        List<Partido> partidos = partidoRepository.findClasificacion(idCompeticion);

        if (partidos.isEmpty()) {
            return new ClasificacionResponse(
                    competicion.getIdCompeticion(),
                    competicion.getNombre(),
                    new ArrayList<>()
            );
        }

        List<Equipo> clubesParticipantes = competicion.getClubesParticipantes();

        boolean tieneGrupos = partidos.stream()
                .anyMatch(p -> p.getFase() != null && FASES_GRUPO.contains(p.getFase()));

        if (tieneGrupos) {
            return procesarConGrupos(competicion, partidos, clubesParticipantes);
        } else {
            return procesarTablaUnica(competicion, partidos, clubesParticipantes);
        }
    }

    private boolean esFaseGrupo(FaseTorneo fase) {
        return fase != null && FASES_GRUPO.contains(fase);
    }

    private ClasificacionGruposResponse procesarConGrupos(
            Competicion competicion,
            List<Partido> partidos,
            List<Equipo> clubesParticipantes) {

        Map<FaseTorneo, List<Partido>> partidosPorGrupo = partidos.stream()
                .filter(p -> p.getFase() != null && esFaseGrupo(p.getFase()))
                .collect(Collectors.groupingBy(Partido::getFase));

        List<GrupoClasificacion> grupos = new ArrayList<>();

        // ORDENAR los grupos alfabéticamente (A, B, C, ...)
        List<FaseTorneo> gruposOrdenados = new ArrayList<>(partidosPorGrupo.keySet());
        gruposOrdenados.sort(Comparator.comparing(FaseTorneo::name));

        for (FaseTorneo grupo : gruposOrdenados) {
            List<Partido> partidosGrupo = partidosPorGrupo.get(grupo);

            Set<Equipo> equiposGrupo = partidosGrupo.stream()
                    .flatMap(p -> List.of(p.getEquipoLocal(), p.getEquipoVisitante()).stream())
                    .collect(Collectors.toSet());

            Map<UUID, Object[]> mapa = new HashMap<>();

            for (Equipo club : equiposGrupo) {
                mapa.put(club.getIdEquipo(), new Object[]{
                        club.getNombre(),
                        0, 0, 0, 0, 0, 0, 0
                });
            }

            for (Partido partido : partidosGrupo) {
                if (!partido.haFinalizado() && !partido.estaEnCurso()) continue;
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

            List<EquipoClasificacion> tablaGrupo = mapa.entrySet().stream()
                    .map(entryEquipo -> {
                        Object[] stats = entryEquipo.getValue();
                        int golesFavor = (int) stats[5];
                        int golesContra = (int) stats[6];
                        return new EquipoClasificacion(
                                entryEquipo.getKey(),
                                (String) stats[0],
                                (int) stats[1],
                                (int) stats[2],
                                (int) stats[3],
                                (int) stats[4],
                                golesFavor,
                                golesContra,
                                golesFavor - golesContra,
                                (int) stats[7]
                        );
                    })
                    .sorted(comparadorTabla())
                    .collect(Collectors.toList());

            grupos.add(new GrupoClasificacion(grupo, tablaGrupo));
        }

        return new ClasificacionGruposResponse(
                competicion.getIdCompeticion(),
                competicion.getNombre(),
                grupos
        );
    }

    // ============================================================
    // PROCESAR TABLA ÚNICA - Devuelve ClasificacionResponse
    // ============================================================
    private ClasificacionResponse procesarTablaUnica(
            Competicion competicion,
            List<Partido> partidos,
            List<Equipo> clubesParticipantes) {

        List<Partido> partidosValidos = partidos.stream()
                .filter(p -> p.getFase() == null || FASES_LIGA.contains(p.getFase()))
                .collect(Collectors.toList());

        Map<UUID, Object[]> mapa = new HashMap<>();

        for (Equipo club : clubesParticipantes) {
            mapa.put(club.getIdEquipo(), new Object[]{
                    club.getNombre(),
                    0, 0, 0, 0, 0, 0, 0
            });
        }

        for (Partido partido : partidosValidos) {
            if (!partido.haFinalizado() && !partido.estaEnCurso()) continue;

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
                    int golesFavor = (int) stats[5];
                    int golesContra = (int) stats[6];
                    return new EquipoClasificacion(
                            entry.getKey(),
                            (String) stats[0],
                            (int) stats[1],
                            (int) stats[2],
                            (int) stats[3],
                            (int) stats[4],
                            golesFavor,
                            golesContra,
                            golesFavor - golesContra,
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

    // ============================================================
    // PROCESAR PARTIDO
    // ============================================================
    private void procesarPartido(Partido partido, UUID idClub, String nombreClub,
                                 int golesFavor, int golesContra,
                                 Map<UUID, Object[]> mapa) {

        Object[] stats = mapa.computeIfAbsent(idClub, k -> new Object[]{
                nombreClub,
                0, 0, 0, 0, 0, 0, 0
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

    // ============================================================
    // PENALES
    // ============================================================
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
        }
    }

    // ============================================================
    // UTILIDADES
    // ============================================================
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