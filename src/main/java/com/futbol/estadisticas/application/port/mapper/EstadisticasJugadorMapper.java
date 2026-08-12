package com.futbol.estadisticas.application.port.mapper;

import com.futbol.estadisticas.application.port.dto.response.EstadisticasCompeticionDTO;
import com.futbol.estadisticas.application.port.dto.response.EstadisticasJugadorResponse;
import com.futbol.estadisticas.application.port.dto.response.EventoPartidoResponse;
import com.futbol.estadisticas.domain.model.Competicion;
import com.futbol.estadisticas.domain.model.EventosPartido;
import com.futbol.estadisticas.domain.model.Jugador;
import com.futbol.estadisticas.domain.model.Partido;
import com.futbol.estadisticas.domain.model.enums.TipoEvento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class EstadisticasJugadorMapper {
    private final EstadisticasCompeticionMapper estadisticaCompeticionMapper;

    public EstadisticasJugadorResponse toResponse(Jugador jugador, List<EventosPartido> eventos) {
        if (eventos == null || eventos.isEmpty()) {
            return buildEmptyResponse(jugador);
        }

        List<EventosPartido> eventosConCompeticion = eventos.stream()
                .filter(e -> e.getPartido() != null)
                .filter(e -> e.getPartido().getCompeticion() != null)
                .toList();

        if (eventosConCompeticion.isEmpty()) {
            return buildEmptyResponse(jugador);
        }

        Map<UUID, List<EventosPartido>> porCompeticion = eventosConCompeticion.stream()
                .collect(Collectors.groupingBy(e -> e.getPartido().getCompeticion().getIdCompeticion()));

        List<EstadisticasCompeticionDTO> porCompeticionDTO = porCompeticion.entrySet().stream()
                .map(entry -> {
                    List<EventosPartido> eventosDeLaCompeticion = entry.getValue();
                    Competicion competicion = eventosDeLaCompeticion.get(0).getPartido().getCompeticion();
                    return estadisticaCompeticionMapper.toDTO(competicion, eventosDeLaCompeticion);
                })
                .sorted(Comparator.comparing(EstadisticasCompeticionDTO::nombreCompeticion))
                .toList();

        // Calcular totales
        int totalPartidos = porCompeticionDTO.stream()
                .mapToInt(EstadisticasCompeticionDTO::partidosJugados)
                .sum();

        int totalMinutos = porCompeticionDTO.stream()
                .mapToInt(EstadisticasCompeticionDTO::minutosJugados)
                .sum();

        int totalGoles = porCompeticionDTO.stream()
                .mapToInt(EstadisticasCompeticionDTO::goles)
                .sum();

        int totalGolesPenal = porCompeticionDTO.stream()
                .mapToInt(EstadisticasCompeticionDTO::golesPenal)
                .sum();

        int totalPenalesFallados = porCompeticionDTO.stream()
                .mapToInt(EstadisticasCompeticionDTO::penalesFallados)
                .sum();

        int totalAutogoles = porCompeticionDTO.stream()
                .mapToInt(EstadisticasCompeticionDTO::autogoles)
                .sum();

        int totalAsistencias = porCompeticionDTO.stream()
                .mapToInt(EstadisticasCompeticionDTO::asistencias)
                .sum();

        int totalAmarillas = porCompeticionDTO.stream()
                .mapToInt(EstadisticasCompeticionDTO::tarjetasAmarillas)
                .sum();

        int totalRojas = porCompeticionDTO.stream()
                .mapToInt(EstadisticasCompeticionDTO::tarjetasRojas)
                .sum();

        return EstadisticasJugadorResponse.builder()
                .idJugador(jugador.getIdPersonal())
                .nombreJugador(jugador.getNombreCompleto())
                .totalPartidosJugados(totalPartidos)
                .totalMinutosJugados(totalMinutos)
                .totalGoles(totalGoles)
                .totalGolesPenal(totalGolesPenal)
                .totalPenalesFallados(totalPenalesFallados)
                .totalAutogoles(totalAutogoles)
                .totalAsistencias(totalAsistencias)
                .totalTarjetasAmarillas(totalAmarillas)
                .totalTarjetasRojas(totalRojas)
                .porCompeticion(porCompeticionDTO)
                .build();
    }

    private EstadisticasJugadorResponse buildEmptyResponse(Jugador jugador) {
        return EstadisticasJugadorResponse.builder()
                .idJugador(jugador.getIdPersonal())
                .nombreJugador(jugador.getNombreCompleto())
                .totalPartidosJugados(0)
                .totalMinutosJugados(0)
                .totalGoles(0)
                .totalGolesPenal(0)
                .totalPenalesFallados(0)
                .totalAutogoles(0)
                .totalAsistencias(0)
                .totalTarjetasAmarillas(0)
                .totalTarjetasRojas(0)
                .porCompeticion(List.of())
                .build();
    }
}
