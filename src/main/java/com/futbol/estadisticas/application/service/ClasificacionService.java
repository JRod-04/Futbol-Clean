package com.futbol.estadisticas.application.service;

import java.util.*;
import java.util.stream.Collectors;

import com.futbol.estadisticas.application.port.dto.response.LideresEstadisticos.*;
import com.futbol.estadisticas.application.port.dto.response.RankingEstadisticasDTO;
import com.futbol.estadisticas.application.port.mapper.LideresEstadisticosMapper;
import com.futbol.estadisticas.domain.model.*;
import com.futbol.estadisticas.domain.model.enums.FaseTorneo;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.futbol.estadisticas.application.port.dto.response.ClasificacionDTO.*;
import com.futbol.estadisticas.application.port.in.ClasificacionUseCase;
import com.futbol.estadisticas.application.port.out.CompeticionRepositoryPort;
import com.futbol.estadisticas.application.port.out.PartidoRepositoryPort;
import com.futbol.estadisticas.domain.model.enums.EstadoPartido;
import com.futbol.estadisticas.domain.model.enums.TipoEvento;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClasificacionService implements ClasificacionUseCase {

    private final PartidoRepositoryPort partidoRepository;
    private final CompeticionRepositoryPort competicionRepository;
    private final LideresEstadisticosMapper lideresEstadisticosMapper;



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

    @Override
    public LideresEstadisticosResponse obtenerLideresEstadisticos(UUID idCompeticion, int limit) {
        Competicion competicion = competicionRepository.findById(idCompeticion)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Competición no encontrada con id: " + idCompeticion));

        List<Partido> partidos = partidoRepository.findByCompeticion(idCompeticion);

        if (partidos.isEmpty()) {
            return LideresEstadisticosResponse.builder()
                    .idCompeticion(idCompeticion)
                    .nombreCompeticion(competicion.getNombre())
                    .estadisticas(List.of())
                    .build();
        }

        Map<UUID, RankingEstadisticasDTO> statsPorJugador = calcularEstadisticas(partidos);

        return lideresEstadisticosMapper.toResponse(
                idCompeticion,
                competicion.getNombre(),
                statsPorJugador,
                limit
        );    }


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
                        0, 0, 0, 0, 0, 0, 0,club.getNombreCorto()
                });
            }

            for (Partido partido : partidosGrupo) {
                if (!partido.haFinalizado() && !partido.estaEnCurso()) continue;
                procesarPartido(partido,
                        partido.getEquipoLocal().getIdEquipo(),
                        partido.getEquipoLocal().getNombre(),
                        partido.getEquipoLocal().getNombreCorto(),
                        partido.getGolesLocal(),
                        partido.getGolesVisitante(),
                        mapa);
                procesarPartido(partido,
                        partido.getEquipoVisitante().getIdEquipo(),
                        partido.getEquipoVisitante().getNombre(),
                        partido.getEquipoVisitante().getNombreCorto(),
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
                                (String) stats[8],
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
                    partido.getEquipoLocal().getNombreCorto(),
                    partido.getGolesLocal(),
                    partido.getGolesVisitante(),
                    mapa);

            procesarPartido(partido,
                    partido.getEquipoVisitante().getIdEquipo(),
                    partido.getEquipoVisitante().getNombre(),
                    partido.getEquipoVisitante().getNombreCorto(),
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
                            (String) stats[8],
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


    private void procesarPartido(Partido partido, UUID idClub, String nombreClub, String nombreCortoClub,
                                 int golesFavor, int golesContra,
                                 Map<UUID, Object[]> mapa) {

        Object[] stats = mapa.computeIfAbsent(idClub, k -> new Object[]{
                nombreClub,
                0, 0, 0, 0, 0, 0, 0, nombreCortoClub
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



    // ============================================================
    // CALCULAR ESTADÍSTICAS POR JUGADOR
    // ============================================================

    private Map<UUID, RankingEstadisticasDTO> calcularEstadisticas(List<Partido> partidos) {
        Map<UUID, RankingEstadisticasDTO> statsMap = new HashMap<>();

        for (Partido partido : partidos) {
            if (!partido.haFinalizado()) continue;

            // Agrupar eventos por jugador
            Map<UUID, List<EventosPartido>> eventosPorJugador = partido.getEventos().stream()
                    .filter(e -> e.getPersonal() != null && e.getPersonal() instanceof Jugador)
                    .collect(Collectors.groupingBy(e -> e.getPersonal().getIdPersonal()));

            for (Map.Entry<UUID, List<EventosPartido>> entry : eventosPorJugador.entrySet()) {
                UUID idJugador = entry.getKey();
                List<EventosPartido> eventos = entry.getValue();
                Jugador jugador = (Jugador) eventos.get(0).getPersonal();

                RankingEstadisticasDTO stats = statsMap.computeIfAbsent(idJugador,
                        k -> crearStatsBase(jugador));

                // Procesar eventos
                for (EventosPartido evento : eventos) {
                    stats = procesarEvento(stats, evento);
                }

                // Calcular minutos jugados
                int minutos = calcularMinutosJugados(partido, eventos);
                stats = stats.withMinutosJugados(stats.minutosJugados() + minutos);

                // Verificar portería a cero (para porteros)
                if (esPortero(jugador) && haMantenidoPorteriaCero(partido, jugador)) {
                    stats = stats.withPorteriasCero(stats.porteriasCero() + 1);
                }

                statsMap.put(idJugador, stats);
            }
        }

        return statsMap;
    }

    // ============================================================
    // PROCESAR EVENTO INDIVIDUAL
    // ============================================================

    private RankingEstadisticasDTO procesarEvento(RankingEstadisticasDTO stats, EventosPartido evento) {
        switch (evento.getTipoEvento()) {
            case GOL:
                return stats.withGoles(stats.goles() + 1);
            case AUTOGOL:
                return stats.withAutogoles(stats.autogoles() + 1);
            case PENALTI_ANOTADO:
                return stats
                        .withGolesPenal(stats.golesPenal() + 1)
                        .withPenalesAnotados(stats.penalesAnotados() + 1)
                        .withGoles(stats.goles() + 1);
            case PENALTI_FALLADO:
                return stats.withPenalesFallados(stats.penalesFallados() + 1);
            case PENALTI_CONCEDIDO:
                return stats.withPenalesConseguidos(stats.penalesConseguidos() + 1);
            case ASISTENCIA:
                return stats.withAsistencias(stats.asistencias() + 1);
            case AMARILLA:
                return stats.withTarjetasAmarillas(stats.tarjetasAmarillas() + 1);
            case ROJA:
                return stats.withTarjetasRojas(stats.tarjetasRojas() + 1);
            case TIRO_A_PUERTA:
                return stats.withTirosAPuerta(stats.tirosAPuerta() + 1);
            case TIRO_FUERA:
                return stats.withTirosFuera(stats.tirosFuera() + 1);
            case PARADA:
                return stats.withParadas(stats.paradas() + 1);
            default:
                return stats;
        }
    }

    // ============================================================
    // CREAR STATS BASE
    // ============================================================

    private RankingEstadisticasDTO crearStatsBase(Jugador jugador) {
        Equipo equipo = jugador.getEquipoActual();
        String posicionEnCampo = obtenerPosicionEnCampo(jugador);

        return RankingEstadisticasDTO.builder()
                .idJugador(jugador.getIdPersonal())
                .nombre(jugador.getNombre())
                .apellido(jugador.getApellido())
                .nombreCompleto(jugador.getNombreCompleto())
                .dorsal(jugador.getDatosDeportivos() != null ?
                        jugador.getDatosDeportivos().getDorsal() : null)
                .nombreEquipo(equipo != null ? equipo.getNombre() : null)
                .idEquipo(equipo != null ? equipo.getIdEquipo() : null)
                .minutosJugados(0)
                .partidosJugados(0)
                .goles(0)
                .golesPenal(0)
                .autogoles(0)
                .asistencias(0)
                .tirosAPuerta(0)
                .tirosFuera(0)
                .penalesAnotados(0)
                .penalesConseguidos(0)
                .penalesFallados(0)
                .tarjetasAmarillas(0)
                .tarjetasRojas(0)
                .paradas(0)
                .porteriasCero(0)
                .build();
    }

    // ============================================================
    // OBTENER POSICIÓN EN EL CAMPO
    // ============================================================

    private String obtenerPosicionEnCampo(Jugador jugador) {
        if (jugador.getDatosDeportivos() == null) {
            return "Sin posición";
        }
        PosicionJugador posicion = jugador.getDatosDeportivos().getPosicionActual();
        return posicion != null ? posicion.getDisplayName() : "Sin posición";
    }

    // ============================================================
    // VERIFICAR SI ES PORTERO
    // ============================================================

    private boolean esPortero(Jugador jugador) {
        return jugador.getDatosDeportivos() != null &&
                jugador.getDatosDeportivos().getPosiciones() != null &&
                jugador.getDatosDeportivos().getPosiciones().contains(PosicionJugador.PORTERO);
    }

    // ============================================================
    // VERIFICAR PORTERÍA A CERO
    // ============================================================

    private boolean haMantenidoPorteriaCero(Partido partido, Jugador portero) {
        UUID idEquipo = portero.getEquipoActual().getIdEquipo();
        boolean equipoLocal = partido.getEquipoLocal().getIdEquipo().equals(idEquipo);

        if (equipoLocal) {
            return partido.getGolesVisitante() == 0;
        } else {
            return partido.getGolesLocal() == 0;
        }
    }

    // ============================================================
    // CALCULAR MINUTOS JUGADOS
    // ============================================================

    private int calcularMinutosJugados(Partido partido, List<EventosPartido> eventos) {
        EventosPartido entrada = eventos.stream()
                .filter(e -> e.getTipoEvento() == TipoEvento.TITULAR ||
                        e.getTipoEvento() == TipoEvento.SUB_IN)
                .findFirst()
                .orElse(null);

        if (entrada == null || entrada.getMinuto() == null) {
            return 0;
        }

        int minutoEntrada = entrada.getMinuto().getHour() * 60 + entrada.getMinuto().getMinute();

        EventosPartido salida = eventos.stream()
                .filter(e -> e.getTipoEvento() == TipoEvento.SUB_OUT)
                .findFirst()
                .orElse(null);

        if (salida != null && salida.getMinuto() != null) {
            int minutoSalida = salida.getMinuto().getHour() * 60 + salida.getMinuto().getMinute();
            return Math.max(0, minutoSalida - minutoEntrada);
        }

        // Buscar fin del partido
        EventosPartido finPartido = partido.getEventos().stream()
                .filter(e -> e.getTipoEvento() == TipoEvento.FIN_PARTIDO)
                .findFirst()
                .orElse(null);

        if (finPartido != null && finPartido.getMinuto() != null) {
            int minutoFin = finPartido.getMinuto().getHour() * 60 + finPartido.getMinuto().getMinute();
            return Math.max(0, minutoFin - minutoEntrada);
        }

        return Math.max(0, 90 - minutoEntrada);
    }







}