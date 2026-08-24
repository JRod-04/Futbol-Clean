package com.futbol.estadisticas.application.port.mapper;

import com.futbol.estadisticas.application.port.dto.response.EstadisticasCompeticionDTO;
import com.futbol.estadisticas.application.port.dto.response.LideresEstadisticos;
import com.futbol.estadisticas.application.port.dto.response.LideresEstadisticos.*;
import com.futbol.estadisticas.application.port.dto.response.RankingEstadisticasDTO;
import com.futbol.estadisticas.application.port.out.JugadorRepositoryPort;
import com.futbol.estadisticas.domain.model.Jugador;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LideresEstadisticosMapper {
    public LideresEstadisticosResponse toResponse(
            UUID idCompeticion,
            String nombreCompeticion,
            Map<UUID, RankingEstadisticasDTO> statsPorJugador,
            int limit) {

        if (statsPorJugador == null || statsPorJugador.isEmpty()) {
            return LideresEstadisticosResponse.builder()
                    .idCompeticion(idCompeticion)
                    .nombreCompeticion(nombreCompeticion)
                    .estadisticas(List.of())
                    .build();
        }

        List<EstadisticaLideresDTO> estadisticas = new ArrayList<>();

        estadisticas.add(crearEstadisticaLideres("Goles", statsPorJugador, limit,
                RankingEstadisticasDTO::goles,
                RankingEstadisticasDTO::nombreCompleto));

        estadisticas.add(crearEstadisticaLideres("Asistencias", statsPorJugador, limit,
                RankingEstadisticasDTO::asistencias,
                RankingEstadisticasDTO::nombreCompleto));

        estadisticas.add(crearEstadisticaLideres("Minutos Jugados", statsPorJugador, limit,
                RankingEstadisticasDTO::minutosJugados,
                RankingEstadisticasDTO::nombreCompleto));

        estadisticas.add(crearEstadisticaLideres("Penaltis Anotados", statsPorJugador, limit,
                RankingEstadisticasDTO::penalesAnotados,
                RankingEstadisticasDTO::nombreCompleto));

        estadisticas.add(crearEstadisticaLideres("Penaltis Conseguidos", statsPorJugador, limit,
                RankingEstadisticasDTO::penalesConseguidos,
                RankingEstadisticasDTO::nombreCompleto));

        estadisticas.add(crearEstadisticaLideres("Penaltis Fallados", statsPorJugador, limit,
                RankingEstadisticasDTO::penalesFallados,
                RankingEstadisticasDTO::nombreCompleto));

        estadisticas.add(crearEstadisticaLideres("Tarjetas Amarillas", statsPorJugador, limit,
                RankingEstadisticasDTO::tarjetasAmarillas,
                RankingEstadisticasDTO::nombreCompleto));

        estadisticas.add(crearEstadisticaLideres("Tarjetas Rojas", statsPorJugador, limit,
                RankingEstadisticasDTO::tarjetasRojas,
                RankingEstadisticasDTO::nombreCompleto));

        estadisticas.add(crearEstadisticaLideres("Paradas", statsPorJugador, limit,
                RankingEstadisticasDTO::paradas,
                RankingEstadisticasDTO::nombreCompleto));

        estadisticas.add(crearEstadisticaLideres("Porterías a Cero", statsPorJugador, limit,
                RankingEstadisticasDTO::porteriasCero,
                RankingEstadisticasDTO::nombreCompleto));

        estadisticas.add(crearEstadisticaLideresDecimal("Efectividad de Tiros", statsPorJugador, limit,
                RankingEstadisticasDTO::getEfectividadTiros,
                RankingEstadisticasDTO::nombreCompleto));

        estadisticas.add(crearEstadisticaLideres("Tiros a Puerta", statsPorJugador, limit,
                RankingEstadisticasDTO::tirosAPuerta,
                RankingEstadisticasDTO::nombreCompleto));

        return LideresEstadisticosResponse.builder()
                .idCompeticion(idCompeticion)
                .nombreCompeticion(nombreCompeticion)
                .estadisticas(estadisticas)
                .build();
    }

    // ============================================================
    // MÉTODOS PARA CREAR ESTADÍSTICAS
    // ============================================================

    private EstadisticaLideresDTO crearEstadisticaLideres(
            String titulo,
            Map<UUID, RankingEstadisticasDTO> statsMap,
            int limit,
            ToIntFunction<RankingEstadisticasDTO> valueExtractor,
            Function<RankingEstadisticasDTO, String> tieBreaker) {

        List<JugadorLiderDTO> jugadores = statsMap.values().stream()
                .filter(s -> valueExtractor.applyAsInt(s) > 0)
                .sorted((a, b) -> {
                    int compare = Integer.compare(
                            valueExtractor.applyAsInt(b),
                            valueExtractor.applyAsInt(a)
                    );
                    if (compare != 0) return compare;
                    return tieBreaker.apply(a).compareTo(tieBreaker.apply(b));
                })
                .limit(limit)
                .map(s -> JugadorLiderDTO.builder()
                        .idJugador(s.idJugador())
                        .nombre(s.nombre())
                        .apellido(s.apellido())
                        .nombreCompleto(s.nombreCompleto())
                        .dorsal(s.dorsal())
                        .nombreEquipo(s.nombreEquipo())
                        .idEquipo(s.idEquipo())
                        .valorInt(valueExtractor.applyAsInt(s))
                        .build())
                .collect(Collectors.toList());

        return EstadisticaLideresDTO.builder()
                .tituloEstadistica(titulo)
                .jugadores(jugadores)
                .build();
    }

    private EstadisticaLideresDTO crearEstadisticaLideresDecimal(
            String titulo,
            Map<UUID, RankingEstadisticasDTO> statsMap,
            int limit,
            ToDoubleFunction<RankingEstadisticasDTO> valueExtractor,
            Function<RankingEstadisticasDTO, String> tieBreaker) {

        List<JugadorLiderDTO> jugadores = statsMap.values().stream()
                .filter(s -> valueExtractor.applyAsDouble(s) > 0)
                .sorted((a, b) -> {
                    int compare = Double.compare(
                            valueExtractor.applyAsDouble(b),
                            valueExtractor.applyAsDouble(a)
                    );
                    if (compare != 0) return compare;
                    return tieBreaker.apply(a).compareTo(tieBreaker.apply(b));
                })
                .limit(limit)
                .map(s -> JugadorLiderDTO.builder()
                        .idJugador(s.idJugador())
                        .nombre(s.nombre())
                        .apellido(s.apellido())
                        .nombreCompleto(s.nombreCompleto())
                        .dorsal(s.dorsal())
                        .nombreEquipo(s.nombreEquipo())
                        .idEquipo(s.idEquipo())
                        .valorDecimal(valueExtractor.applyAsDouble(s))
                        .build())
                .collect(Collectors.toList());

        return LideresEstadisticos.EstadisticaLideresDTO.builder()
                .tituloEstadistica(titulo)
                .jugadores(jugadores)
                .build();
    }
}
